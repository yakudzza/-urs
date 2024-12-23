package org.example.hacaton.serviceTest;


import org.example.hacaton.model.user.*;
import org.example.hacaton.repository.MemberRepository;
import org.example.hacaton.service.impl.MemberServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MemberServiceImplTest {

    @Mock
    private MemberRepository memberRepository;

    @InjectMocks
    private MemberServiceImpl memberService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createMember_Success() {
        // Arrange
        User user = new User();
        user.setId(1L);
        user.setFirstname("John");
        user.setLastname("Doe");

        TypeDev typeDev = TypeDev.BACKEND;

        Member member = Member.builder()
                .user(user)
                .role(Role.MEMBER)
                .typeDev(typeDev)
                .build();

        when(memberRepository.save(any(Member.class))).thenReturn(member);

        // Act
        Member createdMember = memberService.createMember(user, typeDev);

        // Assert
        assertNotNull(createdMember);
        assertEquals(user, createdMember.getUser());
        assertEquals(Role.MEMBER, createdMember.getRole());
        assertEquals(typeDev, createdMember.getTypeDev());
        verify(memberRepository).save(any(Member.class));
    }

    @Test
    void findById_Success() {
        // Arrange
        Long memberId = 1L;

        Member member = new Member();
        member.setId(memberId);

        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));

        // Act
        Member foundMember = memberService.findById(memberId);

        // Assert
        assertNotNull(foundMember);
        assertEquals(memberId, foundMember.getId());
        verify(memberRepository).findById(memberId);
    }

    @Test
    void findById_ThrowsExceptionWhenNotFound() {
        // Arrange
        Long memberId = 1L;
        when(memberRepository.findById(memberId)).thenReturn(Optional.empty());

        // Act & Assert
        Exception exception = assertThrows(RuntimeException.class, () ->
                memberService.findById(memberId)
        );

        assertEquals("Пользователя с такием id не существует", exception.getMessage());
        verify(memberRepository).findById(memberId);
    }


    @Test
    void getMemberIdFromUserId_Success() {
        // Arrange
        Long userId = 1L;
        Long memberId = 2L;

        Member member = new Member();
        member.setId(memberId);

        when(memberRepository.findByUserId(userId)).thenReturn(member);

        // Act
        Long foundMemberId = memberService.getMemberIdFromUserId(userId);

        // Assert
        assertNotNull(foundMemberId);
        assertEquals(memberId, foundMemberId);
        verify(memberRepository).findByUserId(userId);
    }

}