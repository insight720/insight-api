import {ProColumns, ProTable,} from '@ant-design/pro-components';
import {Modal} from 'antd';
import React from 'react';

export type CreateModalProps = {
    onCancel: () => void;
    onSubmit: (values: API.ApiInfoAddRequest) => void;
    createModalOpen: boolean;
    columns: ProColumns<API.ApiInfo>[]
};

/**
 * 新增模态框
 */
const CreateModal: React.FC<CreateModalProps> = (props: CreateModalProps) => {
    return (
        <Modal open={props.createModalOpen} onCancel={props.onCancel} footer={null}>
            <ProTable type='form' columns={props.columns}
                      onSubmit={async (fields: API.ApiInfoAddRequest) => {
                          props.onSubmit(fields);
                      }}/>
        </Modal>
    );
};

export default CreateModal;
