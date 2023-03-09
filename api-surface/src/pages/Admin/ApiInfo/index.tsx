import {PlusOutlined} from '@ant-design/icons';
import type {ActionType, ProColumns, ProDescriptionsItemProps} from '@ant-design/pro-components';
import {FooterToolbar, PageContainer, ProDescriptions, ProTable,} from '@ant-design/pro-components';
import {FormattedMessage, useIntl} from '@umijs/max';
import {Button, Drawer, message} from 'antd';
import React, {useRef, useState} from 'react';
import {
    addApiInfo,
    deleteApiInfo,
    listApiInfoByPage,
    offlineApiInfo,
    onlineApiInfo,
    updateApiInfo
} from "@/services/api-facade/apiInfoController";
import CreateModal from "@/pages/Admin/ApiInfo/components/CreateModal";
import UpdateModal from "@/pages/Admin//ApiInfo/components/UpdateModal";

/**
 * @en-US Add node
 * @zh-CN 添加节点
 * @param fields
 */
/*
const handleAdd = async (fields: API.RuleListItem) => {
  const hide = message.loading('正在添加');
  try {
    await addRule({ ...fields });
    hide();
    message.success('Added successfully');
    return true;
  } catch (error) {
    hide();
    message.error('Adding failed, please try again!');
    return false;
  }
};
*/

/**
 * @en-US Update node
 * @zh-CN 更新节点
 *
 * @param fields
 */
/*
const handleUpdate = async (fields: FormValueType) => {
  const hide = message.loading('Configuring');
  try {
    await updateRule({
      name: fields.name,
      desc: fields.desc,
      key: fields.key,
    });
    hide();

    message.success('Configuration is successful');
    return true;
  } catch (error) {
    hide();
    message.error('Configuration failed, please try again!');
    return false;
  }
};
*/

/**
 *  Delete node
 * @zh-CN 删除节点
 *
 * @param selectedRows
 */
/*
const handleRemove = async (selectedRows: API.RuleListItem[]) => {
  const hide = message.loading('正在删除');
  if (!selectedRows) return true;
  try {
    await removeRule({
      key: selectedRows.map((row) => row.key),
    });
    hide();
    message.success('Deleted successfully and will refresh soon');
    return true;
  } catch (error) {
    hide();
    message.error('Delete failed, please try again');
    return false;
  }
};
*/

const ApiInfoList: React.FC = () => {
    /**
     * @en-US Pop-up window of new window
     * @zh-CN 新建窗口的弹窗
     *  */
    const [createModalOpen, handleCreateModalOpen] = useState<boolean>(false);
    /**
     * @en-US The pop-up window of the distribution update window
     * @zh-CN 分布更新窗口的弹窗
     * */
    const [updateModalOpen, handleUpdateModalOpen] = useState<boolean>(false);

    const [showDetail, setShowDetail] = useState<boolean>(false);

    const actionRef = useRef<ActionType>();
    // const [currentRow, setCurrentRow] = useState<API.RuleListItem>();
    const [currentRow, setCurrentRow] = useState<API.ApiInfo>();
    const [selectedRowsState, setSelectedRows] = useState<API.RuleListItem[]>([]);

    /**
     * @en-US International configuration
     * @zh-CN 国际化配置
     * */
    const intl = useIntl();

    /**
     * @en-US Add node
     * @zh-CN 添加节点
     * @param fields
     */
    const handleAdd = async (fields: API.ApiInfoAddRequest) => {
        const hide = message.loading('正在添加');
        try {
            await addApiInfo({...fields});
            hide();
            handleCreateModalOpen(false);
            message.success('添加成功');
            actionRef.current?.reload();
            return true;
        } catch (error: any) {
            hide();
            message.error(error.message || '添加失败，请重试！');
            return false;
        }
    };

    /**
     * @en-US Update node
     * @zh-CN 更新节点
     *
     * @param fields
     */
    const handleUpdate = async (fields: API.ApiInfoUpdateRequest) => {
        const hide = message.loading('更新中');
        try {
            await updateApiInfo({...fields});
            hide();
            handleUpdateModalOpen(false);
            setCurrentRow(undefined);
            message.success('更新成功');
            actionRef.current?.reload();
            return true;
        } catch (error: any) {
            hide();
            message.error(error.message || '更新失败，请重试！');
            return false;
        }
    };

    /**
     *  Delete node
     * @zh-CN 删除节点
     *
     * @param selectedRow
     */
    const handleRemove = async (selectedRow: API.DeleteRequest) => {
        const hide = message.loading('正在删除');
        if (!selectedRow) return true;
        try {
            await deleteApiInfo({
                id: selectedRow.id,
            });
            hide();
            message.success('删除成功');
            actionRef.current?.reload();
            return true;
        } catch (error: any) {
            hide();
            message.error(error.message || '删除失败，请重试！');
            return false;
        }
    };

    /**
     *  Online or offline node
     * @zh-CN 上线或下线节点
     *
     * @param selectedRow
     * @param online
     */
    const handleOnlineOrOffline = async (selectedRow: API.IdRequest, online: boolean) => {
        const loadingMsg = online ? '正在上线' : '正在下线';
        const successMsg = online ? '上线成功' : '下线成功';
        const errorMsg = online ? '上线失败，请重试！' : '下线失败，请重试！';
        const api = online ? onlineApiInfo : offlineApiInfo;

        const hide = message.loading(loadingMsg);
        if (!selectedRow) return true;
        try {
            await api(selectedRow);
            hide();
            message.success(successMsg);
            actionRef.current?.reload();
            return true;
        } catch (error: any) {
            hide();
            message.error(error.message || errorMsg);
            return false;
        }
    };

    /*
      const columns: ProColumns<API.RuleListItem>[] = [
        {
          title: (
            <FormattedMessage
              id="pages.searchTable.updateForm.ruleName.nameLabel"
              defaultMessage="Rule name"
            />
          ),
          dataIndex: 'name',
          tip: 'The rule name is the unique key',
          render: (dom, entity) => {
            return (
              <a
                onClick={() => {
                  setCurrentRow(entity);
                  setShowDetail(true);
                }}
              >
                {dom}
              </a>
            );
          },
        },
        {
          title: <FormattedMessage id="pages.searchTable.titleDesc" defaultMessage="Description" />,
          dataIndex: 'desc',
          valueType: 'textarea',
        },
        {
          title: (
            <FormattedMessage
              id="pages.searchTable.titleCallNo"
              defaultMessage="Number of service calls"
            />
          ),
          dataIndex: 'callNo',
          sorter: true,
          hideInForm: true,
          renderText: (val: string) =>
            `${val}${intl.formatMessage({
              id: 'pages.searchTable.tenThousand',
              defaultMessage: ' 万 ',
            })}`,
        },
        {
          title: <FormattedMessage id="pages.searchTable.titleStatus" defaultMessage="Status" />,
          dataIndex: 'status',
          hideInForm: true,
          valueEnum: {
            0: {
              text: (
                <FormattedMessage
                  id="pages.searchTable.nameStatus.default"
                  defaultMessage="Shut down"
                />
              ),
              status: 'Default',
            },
            1: {
              text: (
                <FormattedMessage id="pages.searchTable.nameStatus.running" defaultMessage="Running" />
              ),
              status: 'Processing',
            },
            2: {
              text: (
                <FormattedMessage id="pages.searchTable.nameStatus.online" defaultMessage="Online" />
              ),
              status: 'Success',
            },
            3: {
              text: (
                <FormattedMessage
                  id="pages.searchTable.nameStatus.abnormal"
                  defaultMessage="Abnormal"
                />
              ),
              status: 'Error',
            },
          },
        },
        {
          title: (
            <FormattedMessage
              id="pages.searchTable.titleUpdatedAt"
              defaultMessage="Last scheduled time"
            />
          ),
          sorter: true,
          dataIndex: 'updatedAt',
          valueType: 'dateTime',
          renderFormItem: (item, { defaultRender, ...rest }, form) => {
            const status = form.getFieldValue('status');
            if (`${status}` === '0') {
              return false;
            }
            if (`${status}` === '3') {
              return (
                <Input
                  {...rest}
                  placeholder={intl.formatMessage({
                    id: 'pages.searchTable.exception',
                    defaultMessage: 'Please enter the reason for the exception!',
                  })}
                />
              );
            }
            return defaultRender(item);
          },
        },
        {
          title: <FormattedMessage id="pages.searchTable.titleOption" defaultMessage="Operating" />,
          dataIndex: 'option',
          valueType: 'option',
          render: (_, record) => [
            <a
              key="config"
              onClick={() => {
                handleUpdateModalOpen(true);
                setCurrentRow(record);
              }}
            >
              <FormattedMessage id="pages.searchTable.config" defaultMessage="Configuration" />
            </a>,
            <a key="subscribeAlert" href="https://procomponents.ant.design/">
              <FormattedMessage
                id="pages.searchTable.subscribeAlert"
                defaultMessage="Subscribe to alerts"
              />
            </a>,
          ],
        },
      ];
    */
    const columns: ProColumns<API.ApiInfo>[] = [
        {
            title: <FormattedMessage id="index" defaultMessage="序号"/>,
            valueType: 'index',
        },
        {
            title: (
                <FormattedMessage
                    id="name"
                    defaultMessage="名称"
                />
            ),
            dataIndex: 'name',
            tip: 'The rule name is the unique key',
            formItemProps: {
                rules: [{
                    required: true,
                    message: '请输入名称'
                }],
            },
            render: (dom, entity) => {
                return (
                    <a
                        onClick={() => {
                            setCurrentRow(entity);
                            setShowDetail(true);
                        }}
                    >
                        {dom}
                    </a>
                );
            },
        },
        {
            title: <FormattedMessage id="description" defaultMessage="描述"/>,
            dataIndex: 'description',
            valueType: 'textarea',
        },
        {
            title: <FormattedMessage id="method" defaultMessage="请求方法"/>,
            dataIndex: 'method',
            valueType: 'textarea',
        },
        {
            title: <FormattedMessage id="url" defaultMessage="接口地址"/>,
            dataIndex: 'url',
            valueType: 'textarea',
        },
        {
            title: <FormattedMessage id="requestParams" defaultMessage="请求参数"/>,
            dataIndex: 'requestParams',
            valueType: 'jsonCode',
        },
        {
            title: <FormattedMessage id="requestHeader" defaultMessage="请求头"/>,
            dataIndex: 'requestHeader',
            valueType: 'jsonCode',
        },
        {
            title: <FormattedMessage id="responseHeader" defaultMessage="响应头"/>,
            dataIndex: 'responseHeader',
            valueType: 'jsonCode',
        },
        {
            title: <FormattedMessage id="status" defaultMessage="状态"/>,
            dataIndex: 'status',
            hideInForm: true,
            valueEnum: {
                0: {
                    text: (
                        <FormattedMessage
                            id="shutDown"
                            defaultMessage="关闭"
                        />
                    ),
                    status: 'Error',
                },
                1: {
                    text: (
                        <FormattedMessage id="open" defaultMessage="开启"/>
                    ),
                    status: 'Success',
                },
            },
        },
        {
            title: (
                <FormattedMessage
                    id="createTime"
                    defaultMessage="创建时间"
                />
            ),
            dataIndex: 'createTime',
            valueType: 'dateTime',
            hideInForm: true,
        },
        {
            title: (
                <FormattedMessage
                    id="updateTime"
                    defaultMessage="更新时间"
                />
            ),
            dataIndex: 'updateTime',
            valueType: 'dateTime',
            hideInForm: true,
        },
        {
            title: <FormattedMessage id="operation" defaultMessage="操作"/>,
            dataIndex: 'operation',
            valueType: 'option',
            render: (_, record) => [
                <Button
                    key="update"
                    onClick={() => {
                        handleUpdateModalOpen(true);
                        setCurrentRow(record);
                    }}
                >
                    <FormattedMessage id="update" defaultMessage="更新"/>
                </Button>,
                // 接口状态为关闭时显示
                record.status === 0 ? <Button
                    key="online"
                    onClick={() => {
                        handleOnlineOrOffline(record, true);
                    }}
                >
                    <FormattedMessage id="online" defaultMessage="上线"/>
                </Button> : false,
                record.status === 1 ? <Button
                    key="offline"
                    onClick={() => {
                        handleOnlineOrOffline(record, false);
                    }}
                >
                    <FormattedMessage id="offline" defaultMessage="下线"/>
                </Button> : false,
                <Button
                    danger
                    key="remove"
                    onClick={() => {
                        handleRemove(record);
                        setCurrentRow(undefined);
                    }}
                >
                    <FormattedMessage id="remove" defaultMessage="删除"/>
                </Button>,
            ],
        },
    ];

    /**
     * 获取接口信息
     */
    const apiInfo = (async (params: {
        pageSize?: number;
        current?: number;
        keyword?: string;
    }) => {
        const res = await listApiInfoByPage({
            apiInfoQueryRequest: params
        })
        if (res.data) {
            return {
                data: res.data.records || [],
                success: true,
                total: res.data.total,
            }
        } else {
            return {
                data: [],
                success: false,
                total: 0,
            }
        }
    });

    return (
        /*
            <PageContainer>
              <ProTable<API.RuleListItem, API.PageParams>
                headerTitle={intl.formatMessage({
                  id: 'pages.searchTable.title',
                  defaultMessage: 'Enquiry form',
                })}
                actionRef={actionRef}
                rowKey="key"
                search={{
                  labelWidth: 120,
                }}
                toolBarRender={() => [
                  <Button
                    type="primary"
                    key="primary"
                    onClick={() => {
                      handleCreateModalOpen(true);
                    }}
                  >
                    <PlusOutlined /> <FormattedMessage id="pages.searchTable.new" defaultMessage="New" />
                  </Button>,
                ]}
                request={rule}
                columns={columns}
                rowSelection={{
                  onChange: (_, selectedRows) => {
                    setSelectedRows(selectedRows);
                  },
                }}
              />
              {selectedRowsState?.length > 0 && (
                <FooterToolbar
                  extra={
                    <div>
                      <FormattedMessage id="pages.searchTable.chosen" defaultMessage="Chosen" />{' '}
                      <a style={{ fontWeight: 600 }}>{selectedRowsState.length}</a>{' '}
                      <FormattedMessage id="pages.searchTable.item" defaultMessage="项" />
                      &nbsp;&nbsp;
                      <span>
                        <FormattedMessage
                          id="pages.searchTable.totalServiceCalls"
                          defaultMessage="Total number of service calls"
                        />{' '}
                        {selectedRowsState.reduce((pre, item) => pre + item.callNo!, 0)}{' '}
                        <FormattedMessage id="pages.searchTable.tenThousand" defaultMessage="万" />
                      </span>
                    </div>
                  }
                >
                  <Button
                    onClick={async () => {
                      await handleRemove(selectedRowsState);
                      setSelectedRows([]);
                      actionRef.current?.reloadAndRest?.();
                    }}
                  >
                    <FormattedMessage
                      id="pages.searchTable.batchDeletion"
                      defaultMessage="Batch deletion"
                    />
                  </Button>
                  <Button type="primary">
                    <FormattedMessage
                      id="pages.searchTable.batchApproval"
                      defaultMessage="Batch approval"
                    />
                  </Button>
                </FooterToolbar>
              )}
              <ModalForm
                title={intl.formatMessage({
                  id: 'pages.searchTable.createForm.newRule',
                  defaultMessage: 'New rule',
                })}
                width="400px"
                open={createModalOpen}
                onOpenChange={handleCreateModalOpen}
                onFinish={async (value) => {
                  const success = await handleAdd(value as API.RuleListItem);
                  if (success) {
                    handleCreateModalOpen(false);
                    if (actionRef.current) {
                      actionRef.current.reload();
                    }
                  }
                }}
              >
                <ProFormText
                  rules={[
                    {
                      required: true,
                      message: (
                        <FormattedMessage
                          id="pages.searchTable.ruleName"
                          defaultMessage="Rule name is required"
                        />
                      ),
                    },
                  ]}
                  width="md"
                  name="name"
                />
                <ProFormTextArea width="md" name="desc" />
              </ModalForm>
              <UpdateForm
                onSubmit={async (value) => {
                  const success = await handleUpdate(value);
                  if (success) {
                    handleUpdateModalOpen(false);
                    setCurrentRow(undefined);
                    if (actionRef.current) {
                      actionRef.current.reload();
                    }
                  }
                }}
                onCancel={() => {
                  handleUpdateModalOpen(false);
                  if (!showDetail) {
                    setCurrentRow(undefined);
                  }
                }}
                updateModalOpen={updateModalOpen}
                values={currentRow || {}}
              />

              <Drawer
                width={600}
                open={showDetail}
                onClose={() => {
                  setCurrentRow(undefined);
                  setShowDetail(false);
                }}
                closable={false}
              >
                {currentRow?.name && (
                  <ProDescriptions<API.RuleListItem>
                    column={2}
                    title={currentRow?.name}
                    request={async () => ({
                      data: currentRow || {},
                    })}
                    params={{
                      id: currentRow?.name,
                    }}
                    columns={columns as ProDescriptionsItemProps<API.RuleListItem>[]}
                  />
                )}
              </Drawer>
            </PageContainer>
        */
        <PageContainer>
            <ProTable<API.RuleListItem, API.PageParams>
                headerTitle={intl.formatMessage({
                    id: 'pages.searchTable.title',
                    defaultMessage: 'Enquiry form',
                })}
                actionRef={actionRef}
                rowKey="key"
                search={{
                    labelWidth: 120,
                }}
                toolBarRender={() => [
                    <Button
                        type="primary"
                        key="primary"
                        onClick={() => {
                            handleCreateModalOpen(true);
                        }}
                    >
                        <PlusOutlined/> <FormattedMessage id="pages.searchTable.new" defaultMessage="New"/>
                    </Button>,
                ]}
                request={apiInfo}
                columns={columns}
                rowSelection={{
                    onChange: (_, selectedRows) => {
                        setSelectedRows(selectedRows);
                    },
                }}
            />
            {selectedRowsState?.length > 0 && (
                <FooterToolbar
                    extra={
                        <div>
                            <FormattedMessage id="pages.searchTable.chosen" defaultMessage="Chosen"/>{' '}
                            <a style={{fontWeight: 600}}>{selectedRowsState.length}</a>{' '}
                            <FormattedMessage id="pages.searchTable.item" defaultMessage="项"/>
                            &nbsp;&nbsp;
                            <span>
                <FormattedMessage
                    id="pages.searchTable.totalServiceCalls"
                    defaultMessage="Total number of service calls"
                />{' '}
                                {selectedRowsState.reduce((pre, item) => pre + item.callNo!, 0)}{' '}
                                <FormattedMessage id="pages.searchTable.tenThousand" defaultMessage="万"/>
              </span>
                        </div>
                    }
                >
                    <Button
                        onClick={async () => {
                            // await handleRemove(selectedRowsState);
                            setSelectedRows([]);
                            actionRef.current?.reloadAndRest?.();
                        }}
                    >
                        <FormattedMessage
                            id="pages.searchTable.batchDeletion"
                            defaultMessage="Batch deletion"
                        />
                    </Button>
                    <Button type="primary">
                        <FormattedMessage
                            id="pages.searchTable.batchApproval"
                            defaultMessage="Batch approval"
                        />
                    </Button>
                </FooterToolbar>
            )}

            <CreateModal
                onCancel={() => {
                    handleCreateModalOpen(false);
                }}
                onSubmit={(fields: API.ApiInfoAddRequest) => {
                    handleAdd(fields)
                }}
                createModalOpen={createModalOpen}
                columns={columns}
            />

            <UpdateModal
                onCancel={() => {
                    handleUpdateModalOpen(false);
                    if (!showDetail) {
                        setCurrentRow(undefined);
                    }
                }}
                onSubmit={(fields: API.ApiInfoUpdateRequest) => {
                    handleUpdate(fields);
                }}
                updateModalOpen={updateModalOpen}
                columns={columns}
                fields={currentRow || {}}
            />

            <Drawer
                width={600}
                open={showDetail}
                onClose={() => {
                    setCurrentRow(undefined);
                    setShowDetail(false);
                }}
                closable={false}
            >
                {currentRow?.name && (
                    <ProDescriptions<API.RuleListItem>
                        column={2}
                        title={currentRow?.name}
                        request={async () => ({
                            data: currentRow || {},
                        })}
                        params={{
                            id: currentRow?.name,
                        }}
                        columns={columns as ProDescriptionsItemProps<API.RuleListItem>[]}
                    />
                )}
            </Drawer>
        </PageContainer>
    );
};

export default ApiInfoList;
