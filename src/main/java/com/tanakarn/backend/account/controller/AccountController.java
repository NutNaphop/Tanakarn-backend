package com.tanakarn.backend.account.controller;

import com.tanakarn.backend.auth.dto.request.AuthRequest;
import com.tanakarn.backend.account.dto.request.CreateAccountRequest;
import com.tanakarn.backend.auth.dto.response.LoginResponse;
import com.tanakarn.backend.transfer.dto.request.TransferRequest;
import com.tanakarn.backend.common.response.ApiResponse;
import com.tanakarn.backend.account.entity.Account;
import com.tanakarn.backend.transaction.entity.Transaction;
import com.tanakarn.backend.account.repository.AccountRepository;
import com.tanakarn.backend.transaction.repository.TransactionRepository;
import com.tanakarn.backend.account.service.AccountService;
import com.tanakarn.backend.security.jwt.JwtService;
import com.tanakarn.backend.auth.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
public class AccountController  {
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final JwtService jwtService;

    public AccountController(AccountRepository accountRepository, AccountService accountService, UserService userService, TransactionRepository transactionRepository, JwtService jwtService) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
        this.jwtService = jwtService;
    }

    @Operation(summary = "Get account information for testing")
    @GetMapping("/api/account")
    public Account getAccountInfo() {
        return new Account("Naphop", 1000000.00);
    }

    @Operation(summary = "Get all accounts")
    @GetMapping("/api/account/accounts")
    public List<Account> getAllAccounts() {
        return accountRepository.findAll();
    }

    @Operation(summary = "Create a new account")
    @PostMapping("/api/account")
    public Account createAccount(@Valid @RequestBody CreateAccountRequest request) {
        Account newAccount = new Account(request.getName(), request.getInitialBalance());
        return accountRepository.save(newAccount);
    }

    @Operation(summary = "Get my account information")
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/api/account/me")
    public ResponseEntity<Account> getMyAccount(@RequestHeader("Authorization") String token) {
        String jwt = token.substring(7);
        System.out.println(jwt);
        Long userId = jwtService.extractIDFromToken(jwt);
        Account account = accountRepository.findAccountById(userId);

        return ResponseEntity.ok().body(account);
    }

    @Operation(summary = "Get account information by ID")
    @GetMapping("/api/account/{id}")
    public ResponseEntity<Account> getAccountById(@PathVariable Long id) {
        try {
            Account account = accountRepository.findAccountById(id);
            return ResponseEntity.ok(account);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Get my transactions")
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/api/account/me/transactions")
    public ResponseEntity<List<Transaction>> getMyTransactions(@RequestHeader("Authorization") String token) {
        String jwt = token.substring(7);
        Long userId = jwtService.extractIDFromToken(jwt);
        List<Transaction> transactions = transactionRepository.findByAccountIdOrderByTimestampDesc(userId);
        return ResponseEntity.ok().body(transactions);
    }

    @GetMapping("/api/account/transactions/{id}")
    public ResponseEntity<List<Transaction>> getAccountTransactions(@PathVariable Long id) {
        List<Transaction> history = transactionRepository.findByAccountIdOrderByTimestampDesc(id);
        return ResponseEntity.ok(history);
    }
}
