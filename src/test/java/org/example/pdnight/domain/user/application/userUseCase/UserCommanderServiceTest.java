package org.example.pdnight.domain.user.application.userUseCase;

import org.example.pdnight.domain.user.domain.userDomain.UserCommander;
import org.example.pdnight.domain.user.domain.userDomain.UserReader;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserCommanderServiceTest {
    @Mock
    private UserReader userReader;
    @Mock
    private UserCommander userCommander;
    @InjectMocks
    private UserCommanderService userCommanderService;
}
