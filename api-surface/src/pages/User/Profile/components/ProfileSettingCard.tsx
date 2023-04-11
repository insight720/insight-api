import {FooterToolbar, ProCard, ProForm, ProFormText, ProFormTextArea,} from '@ant-design/pro-components';
import React, {useState} from "react";
import {message, Typography, Upload, UploadFile, UploadProps} from "antd";
import {LoadingOutlined, PlusOutlined} from "@ant-design/icons";
import {RcFile, UploadChangeParam} from "antd/es/upload";
import Cookies from "js-cookie";
import {setProfile} from "@/services/api-security/userProfileController";
import {flushSync} from "react-dom";
import {ProFormGroup} from "@ant-design/pro-form/lib";

/**
 * 用户资料设置卡片属性
 */
export type ProfileSettingCardProps = {
    currentUser?: API.LoginUserDTO;
    fetchUserInfo?: () => Promise<API.LoginUserDTO | undefined>;
    setInitialState?: (initialState: (s: any) => any) => void
};

/**
 * 用户资料设置卡片
 */
const ProfileSettingCard: React.FC<ProfileSettingCardProps> = (props: ProfileSettingCardProps) => {
    // 当前登录用户
    const {currentUser} = props;

    // 获取登录用户信息
    const {fetchUserInfo} = props;

    // 设置全局初始状态
    const {setInitialState} = props;

    // 头像加载中状态
    const [avatarLoading, setAvatarLoading] = useState(false);

    // 新设置的头像地址
    const [newAvatar, setNewAvatar] = useState<string>();

    /**
     * 判断是否为空对象（null，undefined，没有属性值）
     * @param value
     * @return 如果为空对象（即 null、undefined、没有属性值），返回 true，否则返回 false
     */
    function isEmptyObject(value: { [key: string]: any } | null | undefined) {
        return value && Object.keys(value).length === 0 && value.constructor === Object;
    }

    /**
     * 资料设置表单提交
     */
    const submitSettingForm = async (fields: any) => {
        if (isEmptyObject(fields) && newAvatar === undefined) {
            // 没有设置新头像或其他项
            message.error("你没有修改任何资料！");
            return false;
        }
        // 将空字符串替换为 null
        Object.keys(fields).forEach(key => {
            if (fields[key] === "") {
                fields[key] = null;
            }
        });
        // 检查所有属性都为 null 的情况
        const values = Object.values(fields);
        if (values.every(value => value === null)) {
            message.error("不能提交全部为空的表单！");
            return false;
        }
        message.loading('修改中');
        try {
            await setProfile({
                ...fields,
                avatar: newAvatar,
                profileId: currentUser?.profileId
            });
            // 已设置新头像，将其链接清除
            setNewAvatar(undefined);
            // 获取新的用户信息，并设置到全局状态中
            const newCurrentUser = await fetchUserInfo?.();
            if (newCurrentUser) {
                flushSync(() => {
                    setInitialState?.((s) => ({
                        ...s,
                        currentUser: newCurrentUser,
                    }));
                });
            }
            message.destroy();
            message.success("用户资料修改成功");
            return true;
        } catch (error: any) {
            message.destroy();
            message.error(error.message || "用户资料修改失败，请稍后再试！");
            return false;
        }
    };

    /**
     * 上传头像前的检查
     */
    const beforeAvatarUpload = (file: RcFile) => {
        const isJpgOrPng = file.type === 'image/jpeg' || file.type === 'image/png';
        if (!isJpgOrPng) {
            message.error('您只能上传 JPG 或 PNG 文件！');
        }
        const isLt5M = file.size / 1024 / 1024 < 7;
        if (!isLt5M) {
            message.error('图像必须小于 7 MB！');
        }
        return isJpgOrPng && isLt5M;
    };

    /**
     * 上传状态改变时的回调
     */
    const onUploadStatusChange: UploadProps['onChange'] = (info: UploadChangeParam<UploadFile>) => {
        const uploadStatus = info.file.status;
        switch (uploadStatus) {
            case "uploading":
                setAvatarLoading(true);
                break;
            case "error":
            case "removed":
                message.destroy();
                message.error(info.file.response.message || "头像上传失败，请稍后再试！")
                break;
            case "done":
            case "success":
                setAvatarLoading(false);
                setNewAvatar(info.file.response.data);
                message.destroy();
                message.success('头像上传成功');
                break;
        }
    };

    /**
     * 头像上传按钮
     */
    const avatarUploadButton = (
        <div>
            {avatarLoading ? <LoadingOutlined/> : <PlusOutlined/>}
            <div style={{marginTop: 8}}>{
                <Typography.Text italic>
                    上传新头像 （JPG 或 PNG 文件，且小于 7 MB）
                </Typography.Text>}
            </div>
        </div>
    );

    return (
        <ProCard>

            <ProForm
                submitter={{
                    render: (_, dom) =>
                        <FooterToolbar>{dom}</FooterToolbar>,
                    onReset: (value) => {
                        // 重置时清除新上传头像的链接
                        setNewAvatar(undefined);
                    }
                }}
                onFinish={async (firmFields) => {
                    // 提交设置表单
                    submitSettingForm(firmFields)
                }}
            >

                <ProForm.Group title="头像和昵称" align="center" tooltip="你可以选择喜欢的头像和昵称">

                    <Upload
                        name="avatar"
                        listType="picture-card"
                        className="avatar-uploader"
                        showUploadList={false}
                        // 后端接口地址
                        action={"/gateway/security/profile/avatar"}
                        // 添加 CSRF 请求头
                        headers={{
                            'X-XSRF-TOKEN': Cookies.get('XSRF-TOKEN') as string
                        }}
                        beforeUpload={beforeAvatarUpload}
                        onChange={onUploadStatusChange}
                    >
                        {
                            newAvatar ?
                                <img src={newAvatar} alt="新头像无法显示" style={{width: '100%'}}/>
                                : avatarUploadButton
                        }
                    </Upload>

                    <ProFormText width="md" tooltip="账户名是唯一的，但昵称可能重复" name="nickname"
                                 label="用户昵称" fieldProps={{defaultValue: currentUser?.nickname}}
                                 rules={[
                                     {
                                         pattern: /^\S.{1,23}\S$/,
                                         message: "昵称必须为 3 到 25 个字符，且不能仅含空白字符",
                                     },
                                 ]}/>

                </ProForm.Group>

                <ProForm.Group title="开发者信息" tooltip="以开发者身份拥有的信息">

                    <ProFormText width="md" tooltip="个人网站可以是你的博客" name='website'
                                 label="个人网站" fieldProps={{defaultValue: currentUser?.website}}
                                 rules={[{
                                     pattern: /^(https?):\/\/((([a-z0-9]|[a-z0-9][a-z0-9\-]*[a-z0-9])\.){1,126}([a-z]|[a-z][a-z0-9\-]*[a-z0-9]))(:\d{1,5})?(\/.*)?$/i,
                                     message: '请输入有效的 URL 地址，包括 http/https 协议头'
                                 }]}/>

                </ProForm.Group>

                <ProFormGroup>

                    <ProFormText width="md" tooltip="你的 Github 主页" name='github'
                                 label="Github 地址" fieldProps={{defaultValue: currentUser?.github}}
                                 rules={[{
                                     pattern: /^https?:\/\/(www\.)?github\.com\/[\w-]+(\/)?$/,
                                     message: "请输入有效的 Github 链接"
                                 }]}/>

                    <ProFormText width="md" tooltip="你的 Gitee 主页" name="gitee"
                                 label="Gitee 地址" fieldProps={{defaultValue: currentUser?.gitee}}
                                 rules={[{
                                     pattern: /^https?:\/\/(www\.)?gitee\.com\/[\w-]+(\/)?$/,
                                     message: "请输入有效的 Gitee 链接"
                                 }]}/>

                </ProFormGroup>

                <ProForm.Group title={"其他信息"}>

                    <ProFormTextArea name="biography" width="xl" tooltip="其他你想提供的个人信息" label="个人简介"
                                     fieldProps={{
                                         defaultValue: currentUser?.biography,
                                         showCount: true,
                                         maxLength: 350
                                     }}/>

                </ProForm.Group>

            </ProForm>

        </ProCard>
    );
};

export default ProfileSettingCard;
