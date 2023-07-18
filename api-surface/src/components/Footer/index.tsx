import {GithubOutlined} from '@ant-design/icons';
import {DefaultFooter} from '@ant-design/pro-components';
import React from 'react';

const Footer: React.FC = () => {

    const currentYear = new Date().getFullYear();

    return (
        <DefaultFooter
            style={{
                background: 'none',
            }}
            copyright={`2023 - ${currentYear} By Luo Fei`}
            links={[
                {
                    key: 'github',
                    title: <GithubOutlined/>,
                    href: 'https://github.com/insight720',
                    blankTarget: true,
                },
                {
                    key: 'Ant Design',
                    title: 'Insight API',
                    href: 'https://github.com/insight720/insight-api',
                    blankTarget: true,
                },
                {
                    key: '备案号',
                    title: '滇ICP备2023000814号-2',
                    href: 'https://beian.miit.gov.cn/#/Integrated/index',
                    blankTarget: true,
                },
            ]}
        />
    );
};

export default Footer;
