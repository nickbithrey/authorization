// js imports
import React from 'react';
import ReactDOM from 'react-dom';
import {
	Container,
	Col
} from 'reactstrap';
import { BrowserRouter as Router, Route } from "react-router-dom";
import { createStore, applyMiddleware } from 'redux';
import { Provider } from 'react-redux';
import reducers from './app/reducer'
import thunkMiddleware from 'redux-thunk'
import Main from './app/main';
import { axiosMiddleware } from './app/axiosMiddleware';

// css imports
import 'bootstrap/dist/css/bootstrap.min.css';

const logger = store => next => action => {
	  console.log('dispatching', action)
	  let result = next(action)
	  console.log('next state', store.getState())
	  return result
};

const store = createStore(
		reducers, 
		applyMiddleware(
				thunkMiddleware,
				axiosMiddleware,
				logger
		)
);

const App = () => {
	return (
			<Provider store={store}>
				<Container>
					<Col>
						<Main />
					</Col>
				</Container>
			</Provider>
	);
};

ReactDOM.render(<App />, document.getElementById("index"));