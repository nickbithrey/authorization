import React from 'react';
import { connect } from 'react-redux';
import Lgn from './Login';
import axios from 'axios';
import { login } from './action';

const mapStateToProps = (state) => {
	const result = {
		success: false
	};
	if (state.authentication.authentication) {
		result.success = state.authentication.authentication.success;
		if (!result.success) {
			result.message = state.authentication.authentication.error;
		}
	}
	return result;
}

const mapDispatchToProps = (dispatch) => {
	return {
		submit: (values) => {
			console.log('login');
			dispatch(login(values));
		}
	};
}

const Login = connect(mapStateToProps, mapDispatchToProps)(Lgn);

export default Login;