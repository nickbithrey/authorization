import { combineReducers } from 'redux';

import authentication from './loginReducer';

const reducers = combineReducers({
	authentication
});

export default reducers;