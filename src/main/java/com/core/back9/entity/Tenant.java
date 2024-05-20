package com.core.back9.entity;

import com.core.back9.common.entity.BaseEntity;
import com.core.back9.entity.constant.Status;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
@Table(name = "tenants")
public class Tenant extends BaseEntity {

	@Column(name = "name")
	private String name;

	@Column(name = "company_number")
	private String companyNumber;

	@Enumerated(EnumType.STRING)
	@Column
	private Status status;

	@Builder
	private Tenant(String name, String companyNumber, Status status) {
		this.name = name;
		this.companyNumber = companyNumber;
		this.status = status;
	}

}
