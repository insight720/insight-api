package pers.project.api.security.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import pers.project.api.common.model.Result;
import pers.project.api.common.util.ResultUtils;
import pers.project.api.common.validation.constraint.FileSpec;
import pers.project.api.security.model.vo.UserProfileSettingVO;
import pers.project.api.security.service.UserProfileService;

import static org.springframework.http.MediaType.IMAGE_JPEG_VALUE;
import static org.springframework.http.MediaType.IMAGE_PNG_VALUE;

/**
 * 用户资料控制器
 *
 * @author Luo Fei
 * @date 2023/03/24
 */
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/profile")
public class UserProfileController {

    private final UserProfileService userProfileService;

    @PutMapping(path = "/setting")
    public Result<Void> setProfile
            (@FileSpec(maxSize = "7MB", mediaTypes = {IMAGE_JPEG_VALUE, IMAGE_PNG_VALUE})
             @RequestPart(required = false) MultipartFile avatarFile,
             @Valid @RequestPart UserProfileSettingVO profileSettingVO) {
        userProfileService.updateUserProfile(avatarFile, profileSettingVO);
        return ResultUtils.success();
    }

}
