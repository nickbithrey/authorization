import  {
	LOGIN_REQUEST,
	LOGIN_SUCCESS,
	LOGIN_FAILURE
} from '../login/action';
import {
	LOGOUT
} from '../main/action';

export default function authentication(state = {}, action) {
	switch (action.type) {
		case LOGIN_REQUEST:
		case LOGOUT:
			return {...state, authentication: {}, isFetching: false};
		case LOGIN_SUCCESS:
			return {...state, authentication: {...action.response.data, success: true}, isFetching: false}
		case LOGIN_FAILURE:
			return {...state, authentication: {error: action.error.message, success: false}, isFetching: false}
		default:
			return state;
	}
}