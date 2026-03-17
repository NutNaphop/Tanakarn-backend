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
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
public class AccountController {
    private final AccountRepository accountRepository;
    private final AccountService accountService;
    private final UserService userService;
    private final TransactionRepository transactionRepository;
    private final JwtService jwtService;

    public AccountController(AccountRepository accountRepository, AccountService accountService, UserService userService, TransactionRepository transactionRepository, JwtService jwtService) {
        this.accountRepository = accountRepository;
        this.accountService = accountService;
        this.userService = userService;
        this.transactionRepository = transactionRepository;
        this.jwtService = jwtService;
    }

    @GetMapping("/api/account")
    public Account getAccountInfo() {
        return new Account("Naphop", 1000000.00);
    }

    @GetMapping("/api/accounts")
    public List<Account> getAllAccounts() {
        return accountRepository.findAll();
    }

    @PostMapping("/api/account")
    public Account createAccount(@Valid @RequestBody CreateAccountRequest request) {
        Account newAccount = new Account(request.getName(), request.getInitialBalance());
        return accountRepository.save(newAccount);
    }

    @GetMapping("/api/account/me")
    public ResponseEntity<Account> getMyAccount(@RequestHeader("Authorization") String token) {
        String jwt = token.substring(7);
        System.out.println(jwt);
        Long userId = jwtService.extractIDFromToken(jwt);
        Account account = accountRepository.findAccountById(userId);

        return ResponseEntity.ok().body(account);
    }

    @GetMapping("/api/account/me/transactions")
    public ResponseEntity<List<Transaction>> getMyTransactions(@RequestHeader("Authorization") String token) {
        String jwt = token.substring(7);
        Long userId = jwtService.extractIDFromToken(jwt);
        List<Transaction> transactions = transactionRepository.findByAccountIdOrderByTimestampDesc(userId);
        return ResponseEntity.ok().body(transactions);
    }

    @PostMapping("/api/transfer")
    @Transactional
    public ResponseEntity<ApiResponse<Void>> transferMoney(@RequestBody TransferRequest request) {
        try {

            accountService.transferMoney(
                    request.getFromAccountId(),
                    request.getToAccountId(),
                    request.getAmount()
            );
            return ResponseEntity.ok().body(new ApiResponse<>(true, "Transfer successful", null));
        } catch (RuntimeException e) {
            // ถ้าเชฟบอกว่าปรุงไม่ได้ (เช่น เงินไม่พอ) ก็ส่งเหตุผลกลับไป
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    @GetMapping("/api/accounts/{id}")
    public ResponseEntity<Account> getAccountById(@PathVariable Long id) {
        try {
            Account account = accountRepository.findAccountById(id);
            return ResponseEntity.ok(account);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/api/accounts/{id}/transactions")
    public ResponseEntity<List<Transaction>> getAccountTransactions(@PathVariable Long id) {
        List<Transaction> history = transactionRepository.findByAccountIdOrderByTimestampDesc(id);
        return ResponseEntity.ok(history);
    }

    @PostMapping("/api/accounts/register")
    public ResponseEntity<ApiResponse<Void>> registerUser(@RequestBody AuthRequest request) {
        try {
            String username = request.getUsername();
            String password = request.getPassword();
            userService.registerUser(username, password);
            return ResponseEntity.ok().body(new ApiResponse<>(true, "User registered successfully", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, e.getMessage(), null));
        }

    }

    @PostMapping("/api/accounts/login")
    public ResponseEntity<ApiResponse<LoginResponse>> loginUser(@RequestBody AuthRequest request){
        try{
            String username = request.getUsername();
            String password = request.getPassword();
            LoginResponse res =  userService.loginUser(username, password);

            return ResponseEntity.ok().body(new ApiResponse<>(true, "Login successful", res));
        }catch (Exception e){
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    @GetMapping("/api/accounts/validate")
    public ResponseEntity<ApiResponse<Void>> validateToken(@RequestHeader("Authorization") String token) {
        try {
            if (jwtService.isValidToken(token.substring(7))) {
                return ResponseEntity.ok().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(401).build();
        }
        return ResponseEntity.status(401).build();
    }
}
