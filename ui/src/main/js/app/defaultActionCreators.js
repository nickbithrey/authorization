export const UPDATE_FIELD = 'UPDATE_FIELD';

const createRequest = (type) => {
	return {
		type: type,
		isFetching: true
	}
}

const createReceipt = (type, response) => {
	return {
		type: type,
		isFetching: false,
		response: response
	}
}

const createErrorReceipt = (type, response) => {
	return {
		type: type,
		isFetching: false,
		error: response
	}
}

export {
	createRequest,
	createReceipt,
	createErrorReceipt
};

