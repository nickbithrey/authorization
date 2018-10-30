import { 
	createRequest, 
	createReceipt, 
	createErrorReceipt 
} from '../defaultActionCreators';

// constants
export const LOGIN_REQUEST = 'LOGIN_REQUEST';
export const LOGIN_SUCCESS = 'LOGIN_SUCCESS';
export const LOGIN_FAILURE = 'LOGIN_FAILURE';
export const LOGIN = 'LOGIN';

// methods

export function login({username, password}) {
	return {
		type: LOGIN,
		url: 'http://localhost:8888/authorization/perform_login',
		method: 'POST',
		headers: {
			username: username, 
			password: password 
		},
		notify: () => createRequest(LOGIN_REQUEST),
		success: (res) => createReceipt(LOGIN_SUCCESS, res),
		failure: (err) => createErrorReceipt(LOGIN_FAILURE, err)
	}
}