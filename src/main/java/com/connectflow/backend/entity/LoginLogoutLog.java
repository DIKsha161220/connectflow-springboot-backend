package com.connectflow.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@Entity
@Table(name = "login_logout_logs")
public class LoginLogoutLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "log_id")
    private Long logId;

    @Column(name = "employee_id")
    private Long employeeId;

    @Column(name = "full_name", length = 150)
    private String fullName;

    @Column(name = "email", length = 255)
    private String email;

    @Column(name = "role_name", length = 50)
    private String roleName;

    @Column(name = "action_type", length = 20)
    private String actionType = "LOGIN";

    @Column(name = "login_time")
    private OffsetDateTime loginTime = OffsetDateTime.now(ZoneOffset.UTC);

    @Column(name = "logout_time")
    private OffsetDateTime logoutTime;

    @Column(name = "created_at")
    private OffsetDateTime createdAt = OffsetDateTime.now(ZoneOffset.UTC);

    @Column(name = "ip_address", length = 45)
    private String ipAddress;

    @Column(name = "status", length = 30)
    private String status = "LOGGED_IN";

    @Column(name = "failure_reason", length = 255)
    private String failureReason;

    public LoginLogoutLog() {
    }

    public LoginLogoutLog(Long employeeId, String fullName, String email, String roleName,
                          String actionType, OffsetDateTime loginTime, OffsetDateTime logoutTime,
                          String ipAddress, String status, String failureReason) {
        this.employeeId = employeeId;
        this.fullName = fullName;
        this.email = email;
        this.roleName = roleName;
        this.actionType = actionType != null ? actionType : "LOGIN";
        this.loginTime = loginTime != null ? loginTime : OffsetDateTime.now(ZoneOffset.UTC);
        this.logoutTime = logoutTime;
        this.createdAt = OffsetDateTime.now(ZoneOffset.UTC);
        this.ipAddress = ipAddress;
        this.status = status != null ? status : "LOGGED_IN";
        this.failureReason = failureReason;
    }

    public Long getLogId() {
        return logId;
    }

    public Long getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Long employeeId) {
        this.employeeId = employeeId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String getActionType() {
        return actionType;
    }

    public void setActionType(String actionType) {
        this.actionType = actionType;
    }

    public OffsetDateTime getLoginTime() {
        return loginTime;
    }

    public void setLoginTime(OffsetDateTime loginTime) {
        this.loginTime = loginTime;
    }

    public OffsetDateTime getLogoutTime() {
        return logoutTime;
    }

    public void setLogoutTime(OffsetDateTime logoutTime) {
        this.logoutTime = logoutTime;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getFailureReason() {
        return failureReason;
    }

    public void setFailureReason(String failureReason) {
        this.failureReason = failureReason;
    }
}
