import React from 'react';
import { Form as ReactForm, Text as ReactText } from 'react-form';
import { Button, Form, FormGroup, Label, Input } from 'reactstrap';

const Login = ({submit, success, message}) => {
	let m;
	if (!success) {
		m = (<p>{message}</p>);
	}
	return (
		<ReactForm onSubmit={values => submit(values)}>
			{formApi => (
				<Form onSubmit={formApi.submitForm}>
					<FormGroup key="username">
		        		<Label for="username">Username</Label>
						<Input field="username" type="text" id="username" name="Username" placeholder="Username" tag={ReactText} />
					</FormGroup>
					<FormGroup key="password">
		        		<Label for="password">Password</Label>
						<Input field="password" type="password" id="password" name="Password" placeholder="Password" tag={ReactText} />
					</FormGroup>
			        <Button type="submit">Login</Button>
			        {m}
				</Form>
			)}
		</ReactForm>
	);
}

export default Login;