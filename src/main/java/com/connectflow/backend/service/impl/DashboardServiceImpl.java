package com.connectflow.backend.service.impl;

import com.connectflow.backend.dto.DashboardResponse;
import com.connectflow.backend.repository.CallLogRepository;
import com.connectflow.backend.repository.EmployeeRepository;
import com.connectflow.backend.repository.FollowUpRepository;
import com.connectflow.backend.repository.InstituteContactRepository;
import com.connectflow.backend.repository.InstituteRepository;
import com.connectflow.backend.repository.VisitRepository;
import com.connectflow.backend.service.DashboardService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class DashboardServiceImpl implements DashboardService {

    private final EmployeeRepository employeeRepository;
    private final InstituteRepository instituteRepository;
    private final InstituteContactRepository contactRepository;
    private final CallLogRepository callLogRepository;
    private final VisitRepository visitRepository;
    private final FollowUpRepository followUpRepository;

    public DashboardServiceImpl(
            EmployeeRepository employeeRepository,
            InstituteRepository instituteRepository,
            InstituteContactRepository contactRepository,
            CallLogRepository callLogRepository,
            VisitRepository visitRepository,
            FollowUpRepository followUpRepository) {
        this.employeeRepository = employeeRepository;
        this.instituteRepository = instituteRepository;
        this.contactRepository = contactRepository;
        this.callLogRepository = callLogRepository;
        this.visitRepository = visitRepository;
        this.followUpRepository = followUpRepository;
    }

    @Override
    public DashboardResponse getDashboard() {
        return new DashboardResponse(
                employeeRepository.count(),
                instituteRepository.count(),
                contactRepository.count(),
                callLogRepository.count(),
                visitRepository.count(),
                followUpRepository.count(),
                followUpRepository.countByFollowUpStatus("Pending"),
                followUpRepository.countByFollowUpStatus("Completed"));
    }
}
