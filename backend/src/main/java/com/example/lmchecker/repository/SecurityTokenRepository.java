package com.example.lmchecker.repository;

import com.example.lmchecker.entity.SecurityToken;
import com.example.lmchecker.entity.TokenType;
import com.example.lmchecker.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SecurityTokenRepository extends JpaRepository<SecurityToken, Long> {

    Optional<SecurityToken> findByToken(String token);

    Optional<SecurityToken> findByTokenAndTokenType(String token, TokenType tokenType);

    @Query("SELECT t FROM SecurityToken t JOIN FETCH t.user u WHERE u.email = :email AND t.tokenType = :tokenType ORDER BY t.createdAt DESC")
    java.util.List<SecurityToken> findByUserEmailAndTokenType(@Param("email") String email, @Param("tokenType") TokenType tokenType);

    @Modifying
    @Query("UPDATE SecurityToken t SET t.consumed = true WHERE t.user = :user AND t.tokenType = :tokenType AND t.consumed = false")
    void invalidateTokensForUser(@Param("user") User user, @Param("tokenType") TokenType tokenType);
}
