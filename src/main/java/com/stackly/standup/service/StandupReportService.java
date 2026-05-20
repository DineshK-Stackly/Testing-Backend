package com.stackly.standup.service;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;


import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.stackly.common.dto.StandupDataDto;
import com.stackly.standup.repository.ResponseRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StandupReportService {

	@Autowired
    private final ResponseRepository standupRepository;

    public byte[] exportReport(Long teamId) {

    	LocalDate today = LocalDate.now();

    	LocalDateTime start = today.atStartOfDay();
    	LocalDateTime end = today.atTime(23, 59, 59);

        List<StandupDataDto> data = standupRepository
                .findTodayReport(teamId,start,end);

        if (data.isEmpty()) {
            throw new RuntimeException("No standup data found for given filters");
        }

//        if ("PDF".equalsIgnoreCase(format)) {
//            return generatePdf(data);
//        } else {
            return generateExcel(data);
//        }
    }
    
    public byte[] generateExcel(List<StandupDataDto> data) {
        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("Standup Report");

            // Header Row
            Row header = sheet.createRow(0);
            String[] columns = {"Team ID", "User ID", "User Name", "Question", "Answer", "Date"};

            for (int i = 0; i < columns.length; i++) {
                Cell cell = header.createCell(i);
                cell.setCellValue(columns[i]);
            }

            // Data Rows
            int rowIdx = 1;
            for (StandupDataDto dto : data) {
                Row row = sheet.createRow(rowIdx++);

                row.createCell(0).setCellValue(dto.getTeamId());
                row.createCell(1).setCellValue(dto.getUserId());
                row.createCell(2).setCellValue(dto.getUserName());
                row.createCell(3).setCellValue(dto.getQuestion());
                row.createCell(4).setCellValue(dto.getAnswer());
                row.createCell(5).setCellValue(dto.getDate().toString());
            }

            workbook.write(out);
            return out.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Failed to generate Excel report", e);
        }
    }

    public byte[] generatePdf(List<StandupDataDto> data) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Document document = new Document();
            PdfWriter.getInstance(document, out);
            document.open();

            document.add(new Paragraph("Standup Report"));

            PdfPTable table = new PdfPTable(6);
            table.setWidthPercentage(100);

            // Header
            addHeader(table, "Team ID");
            addHeader(table, "User ID");
            addHeader(table, "User Name");
            addHeader(table, "Question");
            addHeader(table, "Answer");
            addHeader(table, "Date");

            // Data
            for (StandupDataDto dto : data) {
                table.addCell(String.valueOf(dto.getTeamId()));
                table.addCell(String.valueOf(dto.getUserId()));
                table.addCell(dto.getUserName());
                table.addCell(dto.getQuestion());
                table.addCell(dto.getAnswer());
                table.addCell(dto.getDate().toString());
            }

            document.add(table);
            document.close();

            return out.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Failed to generate PDF report", e);
        }
    }

    private void addHeader(PdfPTable table, String title) {
        PdfPCell header = new PdfPCell();
        header.setPhrase(new Phrase(title));
        header.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(header);
    }
}
