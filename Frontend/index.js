/**
 * @format
 */

import {AppRegistry} from 'react-native';
import App from './src/App';
import {name as appName} from './app.json';
import config from './appconfig.json';
AppRegistry.registerComponent(appName, () => App(config));
