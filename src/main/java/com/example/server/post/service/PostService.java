package com.example.server.post.service;

import com.example.server.post.domain.dto.AllNoticeResponse;
import com.example.server.post.domain.repository.CustomPostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class PostService {
    private final CustomPostRepository customPostRepository;

    public void saveInquiry() {

    }

    public void findInquiryList(String account) {

    }

    public void findInquiryDetail(Long postId, String type) {

    }

    public List<AllNoticeResponse> findAllTypeNotice() {
        return customPostRepository.findAllNotice();
    }

    public String findNoticeContent(Long postId, String type) {
        return customPostRepository.findNoticeContentByIdAndType(postId, type.toUpperCase());
    }
}
