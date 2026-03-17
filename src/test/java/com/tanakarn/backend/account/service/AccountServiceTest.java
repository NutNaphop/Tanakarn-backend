package com.tanakarn.backend.account.service;

import com.tanakarn.backend.account.entity.Account;
import com.tanakarn.backend.account.repository.AccountRepository;
import com.tanakarn.backend.transaction.repository.TransactionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private AccountService accountService;

    @Test
    void shouldTransferMoneySuccessfully() {
        // Arrange
        Account fromAccount = new Account();
        fromAccount.setBalance(1000);

        Account toAccount = new Account();
        toAccount.setBalance(500);

        when(accountRepository.findById(1L)).thenReturn(Optional.of(fromAccount));
        when(accountRepository.findById(2L)).thenReturn(Optional.of(toAccount));

        // Act
        accountService.transferMoney(1L, 2L, 200);

        // Assert
        assertEquals(800, fromAccount.getBalance());
        assertEquals(700, toAccount.getBalance());

        verify(accountRepository).save(fromAccount);
        verify(accountRepository).save(toAccount);
        verify(transactionRepository, times(2)).save(any());
    }

    @Test
    void shouldThrowExceptionWhenAmountIsLessThanOrEqualToZero() {
        // Arrange
        Account fromAccount = new Account();
        fromAccount.setBalance(1000);

        Account toAccount = new Account();
        toAccount.setBalance(500);

        when(accountRepository.findById(1L)).thenReturn(Optional.of(fromAccount));
        when(accountRepository.findById(2L)).thenReturn(Optional.of(toAccount));

        // Act
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                accountService.transferMoney(1L, 2L, 0)
        );

        // Assert
        assertEquals("จำนวนเงินต้องมากกว่า 0", exception.getMessage());
        verify(accountRepository, never()).save(any());
        verify(transactionRepository, never()).save(any());
    }

    @Test
    void shouldThrowExceptionWhenBalanceIsInsufficient() {
        // Arrange
        Account fromAccount = new Account();
        fromAccount.setBalance(100);

        Account toAccount = new Account();
        toAccount.setBalance(500);

        when(accountRepository.findById(1L)).thenReturn(Optional.of(fromAccount));
        when(accountRepository.findById(2L)).thenReturn(Optional.of(toAccount));

        // Act
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                accountService.transferMoney(1L, 2L, 200)
        );

        // Assert
        assertEquals("ยอดเงินไม่พอโอน", exception.getMessage());
        verify(accountRepository, never()).save(any());
    }

    @Test
    void shouldThrowExceptionWhenBalanceIsInNegative() {
        Account fromAccount = new Account();
        fromAccount.setBalance(200);

        Account toAccount = new Account();
        toAccount.setBalance(200);

        when(accountRepository.findById(1L)).thenReturn(Optional.of(fromAccount));
        when(accountRepository.findById(2L)).thenReturn(Optional.of(toAccount));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> accountService.transferMoney(1L, 2L, -10));
        assertEquals("จำนวนเงินต้องมากกว่า 0", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenSourceAccountNotFound() {
        // Arrange
        when(accountRepository.findById(1L)).thenReturn(Optional.empty());

        // Act
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                accountService.transferMoney(1L, 2L, 200)
        );

        // Assert
        assertEquals("ไม่พบบัญชีผู้โอน", exception.getMessage());
        verify(accountRepository, never()).save(any());
        verify(transactionRepository, never()).save(any());
    }

    @Test
    void shouldThrowExceptionWhenReceiveAccountNotFound() {
        // Arrange
        final Account fromAccount = new Account();
        fromAccount.setBalance(1000);

        when(accountRepository.findById(1L)).thenReturn(Optional.of(fromAccount));
        when(accountRepository.findById(2L)).thenReturn(Optional.empty());

        // Act
        RuntimeException exception = assertThrows(RuntimeException.class, () -> accountService.transferMoney(1L, 2L, 200));

        // Assert
        assertEquals("ไม่พบบัญชีผู้รับ", exception.getMessage());
        verify(accountRepository, never()).save(any());
        verify(transactionRepository, never()).save(any());
    }
}