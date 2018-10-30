import { 
	createRequest, 
	createReceipt, 
	createErrorReceipt 
} from '../defaultActionCreators';

// constants
export const LOGOUT = 'LOGOUT';

// methods

export function logout() {
	return {
		type: LOGOUT
	}
}
