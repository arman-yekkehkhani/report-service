package edu.personal.report.controller.report;

import edu.personal.report.model.Report;

import java.util.UUID;


public record ReportDto(
        UUID uuid,
        String title,
        String description
) {
    public static ReportDto fromModel(Report report) {
        return new ReportDto(report.getUuid(), report.getTitle(), report.getDescription());
    }

    public Report toModel() {
        return Report.builder()
                .title(title)
                .description(description)
                .build();
    }
}
