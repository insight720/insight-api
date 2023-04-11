package pers.project.api.security.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pers.project.api.common.model.Result;
import pers.project.api.common.util.ResultUtils;
import pers.project.api.common.validation.constraint.FileSpec;
import pers.project.api.security.model.vo.UserProfileSettingVO;
import pers.project.api.security.service.UserProfileService;

import static org.springframework.http.MediaType.IMAGE_JPEG_VALUE;
import static org.springframework.http.MediaType.IMAGE_PNG_VALUE;
import static org.springframework.util.unit.DataUnit.MEGABYTES;

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

    @PostMapping("/avatar")
    public Result<String> selectAvatar
            (@FileSpec(maxSize = 7, maxSizeUnit = MEGABYTES,
                    mediaTypes = {IMAGE_JPEG_VALUE, IMAGE_PNG_VALUE})
             @RequestPart("avatar") MultipartFile file) {
        String avatarUrl = userProfileService.uploadAvatar(file);
        return ResultUtils.success(avatarUrl);
    }

    @PutMapping("/setting")
    public Result<Void> setProfile(@Valid @RequestBody UserProfileSettingVO profileSettingVO) {
        userProfileService.updateUserProfile(profileSettingVO);
        return ResultUtils.success();
    }

}
