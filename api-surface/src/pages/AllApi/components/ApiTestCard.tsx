import {ProCard, ProDescriptions, ProForm, ProFormText} from '@ant-design/pro-components';
import React, {useEffect, useState} from 'react';
import {Button, Collapse, Drawer, message, Space, Tag, TreeSelect, Typography} from "antd";
import {Panel} from "rc-collapse";
import {viewApiTestFormat} from "@/services/api-facade/apiFormatController";
import {testUserApi} from "@/services/api-security/securityController";
import {useModel} from "@umijs/max";


/**
 * API 调用测试卡片属性
 */
export type ApiTestCardProps = {
    currentUser?: API.LoginUserDTO;
    fetchUserInfo?: () => Promise<API.LoginUserDTO | undefined>;
    setInitialState?: (initialState: (s: any) => any) => void;
    apiDigestVO?: API.ApiDigestVO;
};

/**
 * API 调用测试卡片
 */
const ApiTestCard: React.FC<ApiTestCardProps> = (props: ApiTestCardProps) => {

    // API 摘要信息
    const {apiDigestVO} = props;

    // API 测试调用 VO
    const [apiTestFormatVO,
        setApiTestFormatVO] = useState<API.ApiTestFormatVO>()

    /**
     * HTTP 方法的映射
     */
    const HttpMethodMap: Record<string, { value: string, color: string }> = {
        ['GET']: {
            value: 'GET',
            color: 'green'
        },
        ['HEAD']: {
            value: 'HEAD',
            color: 'blue'
        },
        ['POST']: {
            value: 'POST',
            color: 'magenta'
        },
        ['PUT']: {
            value: 'PUT',
            color: 'geekblue'
        },
        ['DELETE']: {
            value: 'DELETE',
            color: 'red'
        },
        ['OPTIONS']: {
            value: 'OPTIONS',
            color: 'cyan'
        },
        ['TRACE']: {
            value: 'TRACE',
            color: 'purple'
        },
        ['PATCH']: {
            value: 'PATCH',
            color: 'orange'
        }
    };

    // 全局初始状态
    const {initialState} = useModel('@@initialState');

    // 登陆用户信息
    const {currentUser} = initialState || {};

    /**
     * 加载 API 测试调用格式数据
     */
    const loadData = async () => {
        message.loading("加载中");
        try {
            const result = await viewApiTestFormat(
                {
                    digestId: apiDigestVO?.digestId || "",
                }
            )
            setApiTestFormatVO(result.data);
            message.destroy();
            return true;
        } catch (error: any) {
            message.destroy();
            message.error(error.message || '加载失败，请重试！');
            return false;
        }
    };

    useEffect(() => {
        loadData();
    }, []);

    const [visible, setVisible] = useState(false);
    const [result, setResult] = useState<API.ResultUserApiTestVO>();

    // 处理表单提交事件
    const handleSubmit = async (values: any) => {
        const modifiedValues: any = {};
        // 将 requestBody、requestHeader、requestParam、pathVariable 初始化为空对象
        modifiedValues.requestBody = {};
        modifiedValues.requestHeader = {};
        modifiedValues.requestParam = {};
        modifiedValues.pathVariable = {};
        Object.entries(values).forEach(([key, value]) => {
            let modifiedKey = key;
            if (key.startsWith("requestParam.")) {
                modifiedKey = key.replace("requestParam.", "");
                modifiedValues.requestParam[modifiedKey] = value;
            } else if (key.startsWith("requestHeader.")) {
                modifiedKey = key.replace("requestHeader.", "");
                modifiedValues.requestHeader[modifiedKey] = value;
            } else if (key.startsWith("requestBody.")) {
                modifiedKey = key.replace("requestBody.", "");
                modifiedValues.requestBody[modifiedKey] = value;
            } else if (key.startsWith("pathVariable.")) {
                modifiedKey = key.replace("pathVariable.", "");
                modifiedValues.pathVariable[modifiedKey] = value;
            } else {
                modifiedValues[key] = value;
            }
        });
        const requestBody = JSON.stringify(modifiedValues.requestBody);
        const requestHeader = JSON.stringify(modifiedValues.requestHeader);
        const requestParam = JSON.stringify(modifiedValues.requestParam);
        const pathVariable = JSON.stringify(modifiedValues.pathVariable);
        console.log("requestBody");
        console.log(requestBody);
        console.log("requestHeader");
        console.log(requestHeader);
        console.log("requestParam");
        console.log(requestParam);
        console.log("pathVariable");
        console.log(pathVariable);
        message.loading("测试调用中");
        try {
            const result = await testUserApi(
                {
                    accountId: currentUser?.accountId,
                    secretId: currentUser?.secretId || "",
                    digestId: apiDigestVO?.digestId,
                    url: apiDigestVO?.url || "",
                    method: values.method,
                    pathVariable: pathVariable,
                    requestParam: requestParam,
                    requestHeader: requestHeader,
                    requestBody: requestBody
                }
            )
            // 测试结果
            setResult(result);
            setVisible(true);
            message.destroy();
            return true;
        } catch (error: any) {
            message.destroy();
            message.error(error.message || '加载失败，请重试！');
            return false;
        }
    };

    const TestResult = ({result, onClose}: { result: any; onClose: () => void }) => {
        return (
            <Drawer title={<Typography.Text strong>测试调用结果</Typography.Text>}
                    visible closable onClose={onClose}
                    width={700}>
                <ProDescriptions column={1}>
                    <ProDescriptions.Item title={"响应状态码"} valueType={"text"}>
                        {result.data.statusCode}
                    </ProDescriptions.Item>
                    <ProDescriptions.Item title={"响应头"} valueType={"jsonCode"}>
                        {result.data.responseHeader}
                    </ProDescriptions.Item>
                    <ProDescriptions.Item title={"响应体"} valueType={"jsonCode"}>
                        {result.data.responseBody}
                    </ProDescriptions.Item>
                </ProDescriptions>
                <Button onClick={onClose}>返回</Button>
            </Drawer>
        );
    };

    return (
        <div style={{background: '#F5F7FA', display: 'flex'}}>
            <ProCard style={{width: '50%', flexShrink: 0}}>
                <ProDescriptions bordered title="接口测试相关信息"
                                 column={{xxl: 4, xl: 3, lg: 3, md: 3, sm: 2, xs: 1}}>
                    <ProDescriptions.Item label="接口名称" span={1}>
                        {apiDigestVO?.apiName}
                    </ProDescriptions.Item>
                    <ProDescriptions.Item label="请求方法">
                        {apiDigestVO?.methodSet?.map((value) => (
                            <Space key={value}>
                                <Tag color={HttpMethodMap[value || "GET"].color}>
                                    {HttpMethodMap[value || "GET"].value}
                                </Tag>
                            </Space>
                        ))}
                    </ProDescriptions.Item>
                    <ProDescriptions.Item label="接口地址">
                        {apiDigestVO?.url}
                    </ProDescriptions.Item>
                    <ProDescriptions.Item label="接口描述" span={3}>
                        {apiDigestVO?.description}
                    </ProDescriptions.Item>
                    <ProDescriptions.Item label="请求参数" valueType={"jsonCode"} span={3}>
                        {apiTestFormatVO?.requestParam}
                    </ProDescriptions.Item>
                    <ProDescriptions.Item label="请求头" valueType={"jsonCode"} span={3}>
                        {apiTestFormatVO?.requestHeader}
                    </ProDescriptions.Item>
                    <ProDescriptions.Item label="请求体" valueType={"jsonCode"} span={3}>
                        {apiTestFormatVO?.requestBody}
                    </ProDescriptions.Item>
                    <ProDescriptions.Item label="响应头" valueType={"jsonCode"} span={3}>
                        {apiTestFormatVO?.requestHeader}
                    </ProDescriptions.Item>
                    <ProDescriptions.Item label="响应体" valueType={"jsonCode"} span={3}>
                        {apiTestFormatVO?.responseBody}
                    </ProDescriptions.Item>
                </ProDescriptions>
            </ProCard>

            <ProCard style={{width: '50%', flexShrink: 0}} bordered
                     title={<Typography.Text strong>测试调用表单</Typography.Text>}>
                <Typography.Text>
                    这里是一个 API 测试调用的表单。除了请求方法外，填写的每一项下的几个参数都会被转换为 JSON
                    格式字符串的一个属性。其中参数包含路径变量和查询参数两种类型。路径变量是 URL
                    中用花括号括起来的变量，例如：/users/&#123;userId&#125;。查询参数则是使用
                    ?、&、和 = 表示的键值对，例如：/users?name=john&age=30。
                    <br/><br/>
                    在此表单中，您可以根据测试需要添加请求相关内容进行测试调用。
                    使用 JSON 格式的好处是它可以方便地序列化和反序列化数据，而且通常比其他格式更加易读
                    、易于解析和传输。
                    <br/><br/>
                    请注意，当前测试调用的请求体仅支持 JSON 字符串。
                </Typography.Text>
                <br/>
                <br/>
                <ProForm onFinish={handleSubmit}>
                    <Collapse
                        defaultActiveKey={["method", "pathVariable", "requestParam", "requestHeader", "requestBody"]}>
                        <Panel header="请求方法" key={"method"}>
                            <ProFormText name="method"
                                         rules={[{required: true, message: '请选择请求方法'}]}>
                                <TreeSelect
                                    style={{marginBottom: "16px"}}
                                    treeData={apiDigestVO?.methodSet?.map((value) => ({
                                        title: value,
                                        key: value,
                                        value: value,
                                    })) ?? []}
                                    placeholder="选择请求方法"
                                />
                            </ProFormText>
                        </Panel>
                        <Panel header="路径变量" key={"pathVariable"}>
                            {/* 使用正则表达式匹配路径变量的名称 */}
                            {apiDigestVO?.url?.match(/{(.*?)}/g)?.map((variable, index) => {
                                // 去除花括号，获取路径变量的名称
                                const name = variable.slice(1, -1);
                                return <ProFormText key={index} name={`pathVariable.${name}`} label={name}/>;
                            })}
                        </Panel>
                        <Panel header="查询参数" key={"requestParam"}>
                            {/* 使用 map 方法循环遍历 JSON 对象的属性，并嵌套对应的输入框 */}
                            {Object.keys(JSON.parse(apiTestFormatVO?.requestParam || "{}")).map((key) => (
                                <ProFormText key={key} name={`requestParam.${key}`} label={key}/>
                            ))}
                        </Panel>
                        <Panel header="请求头" key={"requestHeader"}>
                            {/* 使用 map 方法循环遍历 JSON 对象的属性，并嵌套对应的输入框 */}
                            {Object.keys(JSON.parse(apiTestFormatVO?.requestHeader || "{}")).map((key) => (
                                <ProFormText key={key} name={`requestHeader.${key}`} label={key}/>
                            ))}
                        </Panel>
                        <Panel header="请求体" key={"requestBody"}>
                            {/* 使用 map 方法循环遍历 JSON 对象的属性，并嵌套对应的输入框 */}
                            {Object.keys(JSON.parse(apiTestFormatVO?.requestBody || "{}")).map((key) => (
                                <ProFormText key={key} name={`requestBody.${key}`} label={key}/>
                            ))}
                        </Panel>
                    </Collapse>
                </ProForm>
            </ProCard>
            {visible && <TestResult result={result} onClose={() => setVisible(false)}/>}
        </div>

    );
};

export default ApiTestCard;