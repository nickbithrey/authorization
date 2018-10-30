import React from 'react';
import Login from '../login/Login.component';
import { BrowserRouter as Router, Route } from "react-router-dom";
import { Button } from 'reactstrap';
import { Link } from "react-router-dom";

const Main = (props) => {
	var loginButton = <Button color="primary" tag={Link} to="/login">Login</Button>
	if (props.authentication.authentication && props.authentication.authentication.success) {
		loginButton = <div><h1>Logged In</h1><Button color="secondary" onClick={props.logout}>Logout</Button></div>
	}
	return (
			<Router>
				<div>
					{loginButton}
					<Route exact path="/login" component={Login} />
				</div>
			</Router>
	);
};

export default Main;