package org.example.pdnight.domain.auth.domain;

import org.example.pdnight.domain.auth.domain.entity.Auth;

public interface AuthCommandQuery {

    Auth save(Auth auth);
}
