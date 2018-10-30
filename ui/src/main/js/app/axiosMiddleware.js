import '@babel/polyfill';
import axios from 'axios';
import oauth from 'axios-oauth-client';
import {
	LOGIN
} from './login/action';
import {
	createReceipt
} from './defaultActionCreators';

export const axiosMiddleware = store => next => action => {
	if (!action.url) {
		return next(action);
	}
	next(action.notify());
// let ax = initAxios(action, store.getState.authentication, next);
	axios({
		method: action.method,
		url: action.url,
		data: action.payload,
		headers: action.headers
	})
	.then(res => {
		return next(action.success(res));
	}, err => {
		return next(action.failure(err));
	});
}

async function initAxios(action, state, next) {
	let auth = state.authentication;
	if (!auth) {
		return axios;
	}
	if (Date.now() - auth.expires_in > auth.time) {
		const a = await getNewToken(); 
		next(createReceipt(LOGIN_SUCCESS, a));
		auth = a
	}
	axios.defaults.headers.common['Authorization'] = auth.token_type + ' ' + auth.access_token;
}
