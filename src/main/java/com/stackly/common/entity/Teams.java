package com.stackly.common.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "Teams")
public class Teams {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="team_Id")
	private Long teamId;
	private String teamName;
	private LocalDateTime createdDate;
	private String createdBy;

}