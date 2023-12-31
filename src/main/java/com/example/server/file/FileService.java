package com.example.server.file;

import com.example.server.member.Member;
import com.example.server.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${cloud.aws.s3.url}")
    private String imageUrl;

    @Transactional
    public String memberImgFileUpload(MultipartFile file) {
        if(Objects.isNull(file)) {
            return null;
        }

        fileApplication.fileMimeTypeCheck(file);

        String translatedImgName = s3Uploader.uploadFile(file);

        return imageUrl + translatedImgName;
    }

    @Transactional
    public String updateMemberImgFile(MultipartFile file, Member member) {
        String currentImg = member.getImg();
        s3Uploader.delete(fileApplication.splitImageUrl(currentImg));

        if(Objects.isNull(file)) {
            memberRepository.updateMemberImg(null, member.getAccount());
            return null;
        }

        fileApplication.fileMimeTypeCheck(file);
        String translatedFileUrl = this.memberImgFileUpload(file);

        memberRepository.updateMemberImg(translatedFileUrl, member.getAccount());
        return translatedFileUrl;
    }

    @Transactional
    public void deleteMemberImgFile(String imageUrl) {
        s3Uploader.delete(fileApplication.splitImageUrl(imageUrl));
    }
}
