package org.example.pdnight.domain.eventParticipant.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@PreAuthorize("hasRole('ADMIN')")
@RestController
@RequestMapping("/api/admin/events/participants")
@RequiredArgsConstructor
public class EventParticipantAdminController {
}
