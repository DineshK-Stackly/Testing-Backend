package com.stackly.standup.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.stackly.standup.service.StandupReportService;


@RestController
@RequestMapping("/api/reports/standup")
public class StandupReportController {

	private final StandupReportService reportService;

	public StandupReportController(StandupReportService reportService) {
		this.reportService = reportService;
	}

	@GetMapping("/export")
	public ResponseEntity<byte[]> exportReport(
			@RequestParam Long teamId) {

		byte[] fileData = reportService.exportReport(teamId);

		String fileName = "standup_report.xlsx" /* + (format.equalsIgnoreCase("PDF") ? "pdf" : "xlsx") */;

		return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName)
				.contentType(MediaType.APPLICATION_OCTET_STREAM)
				.body(fileData);
	}

	private MediaType getMediaType(String format) {
		if ("PDF".equalsIgnoreCase(format)) {
			return MediaType.APPLICATION_PDF;
		}
		return MediaType.APPLICATION_OCTET_STREAM;
	}
}
