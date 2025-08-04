package org.example.pdnight.domain.auth.domain;

import org.example.pdnight.domain.auth.domain.entity.Auth;

public interface AuthCommander {

    Auth save(Auth auth);
}
