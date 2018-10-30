import React from 'react';
import { connect } from 'react-redux';
import Mn from './Main';
import {
	logout
} from './action';

const mapStateToProps = (state = {authentication: {}}) => {
	return state;
}

const mapDispatchToProps = (dispatch) => {
	return {
		logout: (e) => {
			e.preventDefault();
			console.log('logout');
			dispatch(logout());
		}
	};
}

const Main = connect(mapStateToProps, mapDispatchToProps)(Mn);

export default Main;