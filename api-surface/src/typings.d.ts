import {Settings as LayoutSettings} from "@ant-design/pro-layout";

declare module 'slash2';
declare module '*.css';
declare module '*.less';
declare module '*.scss';
declare module '*.sass';
declare module '*.svg';
declare module '*.png';
declare module '*.jpg';
declare module '*.jpeg';
declare module '*.gif';
declare module '*.bmp';
declare module '*.tiff';
declare module 'omit.js';
declare module 'numeral';
declare module '@antv/data-set';
declare module 'mockjs';
declare module 'react-fittext';
declare module 'bizcharts-plugin-slider';

declare const REACT_APP_ENV: 'test' | 'dev' | 'pre' | false;

/**
 * 全局初始状态
 */
interface InitialState {
    settings?: Partial<LayoutSettings>;
    currentUser?: API.LoginUserDTO;
    loading?: boolean;
    fetchUserInfo?: () => Promise<API.LoginUserDTO | undefined>;
}