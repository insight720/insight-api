import {ProColumns, ProFormInstance, ProTable,} from '@ant-design/pro-components';
import {Modal} from 'antd';
import React, {useEffect, useRef} from 'react';

export type updateModalProps = {
    onCancel: () => void;
    onSubmit: (values: API.ApiInfoUpdateRequest) => void;
    updateModalOpen: boolean;
    columns: ProColumns<API.ApiInfoEntity>[]
    fields: API.ApiInfoEntity
};

/**
 * 更新模态框
 */
const updateModal: React.FC<updateModalProps> = (props) => {
    // 获取更新前的数据
    const formRef = useRef<ProFormInstance>();
    useEffect(() => {
        formRef.current?.setFieldsValue(props.fields)
    }, [props.fields]);

    return (
        <Modal open={props.updateModalOpen} onCancel={props.onCancel} footer={null}>
            <ProTable type='form' columns={props.columns}
                      onSubmit={async (fields: API.ApiInfoUpdateRequest) => {
                          // id 不会提交
                          fields.id = props.fields.id;
                          props.onSubmit(fields);
                      }}
                      formRef={formRef}/>
        </Modal>
    );
};

export default updateModal;
