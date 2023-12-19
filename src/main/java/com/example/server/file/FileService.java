package com.example.server.file;

import com.example.server.member.Member;
import com.example.server.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class FileService {
    private final FileApplication fileApplication;
    private final AwsS3Uploader s3Uploader;
    private final MemberRepository memberRepository;

    @Transactional
    public String memberImgFileUpload(MultipartFile multipartFile) {
        if(Objects.isNull(multipartFile)) {
            return null;
        }

        fileApplication.fileMimeTypeCheck(multipartFile);

        return s3Uploader.uploadFile(multipartFile);
    }

    @Transactional
    public void updateMemberImgFile(MultipartFile file, Member member) {
        String imageUrl = member.getImg();

        s3Uploader.delete(imageUrl);

        if(Objects.isNull(file)) {
            memberRepository.updateMemberImg(null, member.getAccount());
            return;
        }

        String translatedFileName = this.memberImgFileUpload(file);
        memberRepository.updateMemberImg(translatedFileName, member.getAccount());
    }

    @Transactional
    public void deleteMemberImgFile(String imageUrl) {
        s3Uploader.delete(imageUrl);
    }
}
