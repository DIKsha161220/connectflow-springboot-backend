package com.connectflow.backend.service.impl;

import com.connectflow.backend.dto.AuditLogResponse;
import com.connectflow.backend.dto.CallLogResponse;
import com.connectflow.backend.dto.EmployeeResponse;
import com.connectflow.backend.dto.FollowUpResponse;
import com.connectflow.backend.dto.InstituteContactResponse;
import com.connectflow.backend.dto.InstituteResponse;
import com.connectflow.backend.dto.RoleResponse;
import com.connectflow.backend.dto.VisitResponse;
import com.connectflow.backend.entity.AuditLog;
import com.connectflow.backend.entity.CallLog;
import com.connectflow.backend.entity.Employee;
import com.connectflow.backend.entity.FollowUp;
import com.connectflow.backend.entity.Institute;
import com.connectflow.backend.entity.InstituteContact;
import com.connectflow.backend.entity.Role;
import com.connectflow.backend.entity.Visit;
import com.connectflow.backend.enums.CallOutcome;
import com.connectflow.backend.enums.CallType;
import com.connectflow.backend.enums.FollowUpStatus;
import com.connectflow.backend.enums.LeadStatus;
import com.connectflow.backend.enums.Priority;
import com.connectflow.backend.enums.VisitStatus;

final class DtoMapper {

    private DtoMapper() {
    }

    static EmployeeResponse employee(Employee employee) {
        return new EmployeeResponse(
                employee.getEmployeeId(),
                employee.getFullName(),
                employee.getEmail(),
                employee.getPhone(),
                employee.getRole().getRoleId(),
                employee.getRole().getRoleName(),
                employee.getActive(),
                employee.getLastLoginAt(),
                employee.getCreatedAt(),
                employee.getUpdatedAt());
    }

    static RoleResponse role(Role role) {
        return new RoleResponse(
                role.getRoleId(),
                role.getRoleName(),
                role.getDescription(),
                role.getActive(),
                role.getCreatedAt(),
                role.getUpdatedAt());
    }

    static InstituteResponse institute(Institute institute) {
        Employee assigned = institute.getAssignedEmployee();
        Employee createdBy = institute.getCreatedBy();
        Employee updatedBy = institute.getUpdatedBy();
        return new InstituteResponse(
                institute.getInstituteId(),
                institute.getInstituteName(),
                institute.getStreetAddress(),
                institute.getVillage(),
                institute.getTaluka(),
                institute.getDistrict(),
                institute.getState(),
                institute.getPinCode(),
                institute.getBranchCount(),
                institute.getReferenceBy(),
                institute.getReferenceFrom(),
                assigned == null ? null : assigned.getEmployeeId(),
                assigned == null ? null : assigned.getFullName(),
                LeadStatus.fromValue(institute.getLeadStatus()),
                Priority.fromValue(institute.getPriority()),
                institute.getNextFollowUpAt(),
                institute.getNotes(),
                institute.getActive(),
                createdBy == null ? null : createdBy.getEmployeeId(),
                updatedBy == null ? null : updatedBy.getEmployeeId(),
                institute.getCreatedAt(),
                institute.getUpdatedAt());
    }

    static InstituteContactResponse contact(InstituteContact contact) {
        return new InstituteContactResponse(
                contact.getContactId(),
                contact.getInstitute().getInstituteId(),
                contact.getContactName(),
                contact.getDesignation(),
                contact.getPhone(),
                contact.getEmail(),
                contact.getPrimary(),
                contact.getNotes(),
                contact.getActive(),
                contact.getCreatedAt(),
                contact.getUpdatedAt());
    }

    static CallLogResponse call(CallLog call) {
        return new CallLogResponse(
                call.getCallId(),
                call.getInstitute().getInstituteId(),
                call.getEmployee().getEmployeeId(),
                call.getContact() == null ? null : call.getContact().getContactId(),
                CallType.fromValue(call.getCallType()),
                call.getCallDatetime(),
                call.getContactPerson(),
                call.getContactPhone(),
                CallOutcome.fromValue(call.getCallOutcome()),
                call.getNextFollowUpAt(),
                call.getNotes(),
                call.getCreatedAt(),
                call.getUpdatedAt());
    }

    static VisitResponse visit(Visit visit) {
        return new VisitResponse(
                visit.getVisitId(),
                visit.getInstitute().getInstituteId(),
                visit.getEmployee().getEmployeeId(),
                visit.getContact() == null ? null : visit.getContact().getContactId(),
                visit.getVisitDatetime(),
                visit.getContactPerson(),
                visit.getContactPhone(),
                VisitStatus.fromValue(visit.getVisitStatus()),
                visit.getPurpose(),
                visit.getNotes(),
                visit.getCreatedAt(),
                visit.getUpdatedAt());
    }

    static FollowUpResponse followUp(FollowUp followUp) {
        return new FollowUpResponse(
                followUp.getFollowUpId(),
                followUp.getInstitute().getInstituteId(),
                followUp.getEmployee().getEmployeeId(),
                followUp.getRelatedCall() == null ? null : followUp.getRelatedCall().getCallId(),
                followUp.getScheduledAt(),
                FollowUpStatus.fromValue(followUp.getFollowUpStatus()),
                followUp.getNotes(),
                followUp.getCompletedAt(),
                followUp.getCompletedBy() == null ? null : followUp.getCompletedBy().getEmployeeId(),
                followUp.getCreatedBy() == null ? null : followUp.getCreatedBy().getEmployeeId(),
                followUp.getUpdatedBy() == null ? null : followUp.getUpdatedBy().getEmployeeId(),
                followUp.getCreatedAt(),
                followUp.getUpdatedAt());
    }

    static AuditLogResponse audit(AuditLog auditLog) {
        return new AuditLogResponse(
                auditLog.getAuditId(),
                auditLog.getEmployee() == null ? null : auditLog.getEmployee().getEmployeeId(),
                auditLog.getEntityType(),
                auditLog.getEntityId(),
                auditLog.getAction(),
                auditLog.getOldValues(),
                auditLog.getNewValues(),
                auditLog.getIpAddress() == null ? null : auditLog.getIpAddress().getHostAddress(),
                auditLog.getCreatedAt());
    }
}
