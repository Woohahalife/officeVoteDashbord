package com.core.back9.entity;

import com.core.back9.common.entity.BaseEntity;
import com.core.back9.dto.MemberDTO;
import com.core.back9.entity.constant.ComplaintStatus;
import com.core.back9.entity.constant.Status;
import com.core.back9.exception.ApiErrorCode;
import com.core.back9.exception.ApiException;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
@Table(name = "complaints")
public class Complaint extends BaseEntity {

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "room_id", nullable = false, updatable = false)
	private Room room;

	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
	@JoinColumn(name = "member_id", nullable = false, updatable = false)
	private Member member;

	@Column(name = "complaint_message", nullable = false)
	private String complaintMessage;

	@Enumerated(EnumType.STRING)
	@Column(name = "status", nullable = false)
	private Status status = Status.REGISTER;

	@Enumerated(EnumType.STRING)
	@Column(name = "complaint_status", nullable = false)
	private ComplaintStatus complaintStatus = ComplaintStatus.IN_PROGRESS;

	@Column(name = "completed_message")
	private String completedMessage;

	@Builder
	public Complaint(Room room, Member member,
					 String complaintMessage, Status status,
					 ComplaintStatus complaintStatus, String completedMessage
	) {
		this.room = room;
		this.member = member;
		this.complaintMessage = complaintMessage;
		this.status = status;
		this.complaintStatus = complaintStatus;
		this.completedMessage = completedMessage;
	}

	public Complaint updateComplaintStatus(ComplaintStatus complaintStatus) {
		this.complaintStatus = complaintStatus;
		return this;
	}

	public Complaint completeComplaint(MemberDTO.Info member, String completeMessage) {
		if (member.isOwner()) {
			this.complaintStatus = ComplaintStatus.COMPLETED;
			if (completeMessage == null || completeMessage.isEmpty()) {
				this.completedMessage = "요청하신 민원이 처리 완료됐습니다.";
			} else {
				this.completedMessage = completeMessage;
			}
			return this;
		}
		throw new ApiException(ApiErrorCode.DO_NOT_HAVE_PERMISSION);
	}

	public Complaint rejectComplaint(MemberDTO.Info member, String rejectMessage) {
		if (member.isOwner()) {
			this.complaintStatus = ComplaintStatus.REJECTED;
			if (rejectMessage == null || rejectMessage.isEmpty()) {
				this.completedMessage = "요청하신 민원이 반려되었습니다.";
			} else {
				this.completedMessage = rejectMessage;
			}
			return this;
		}
		throw new ApiException(ApiErrorCode.DO_NOT_HAVE_PERMISSION);
	}

	public void delete() {
		this.status = Status.UNREGISTER;
	}

	public boolean isPossibleToDelete(MemberDTO.Info user) {
		return this.member.getId().equals(user.getId()) && complaintStatus == ComplaintStatus.PENDING;
	}

}
