import React from "react";
import {Link} from 'react-router-dom';
import "../style/app.css";

class App extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            name: "",
            description: "",
            supplier: "",
            filter: 'ALL',
            products: props.products || []
        };
        this.removeProduct = this.removeProduct.bind(this);
        this.toggleAvailable = this.toggleAvailable.bind(this);
    }

    addProduct() {
        let {name, description, supplier} = this.state;
        if (name) {
            let product = {name, description, available: true, supplier};
            this.setState({products: [...this.state.products, product], name: '', description: '', supplier: ''});
        }
    }

    removeProduct(product) {
        this.setState({
            products: this.state.products.filter(item => product.name !== item.name)
        });
    }

    toggleAvailable(product) {
        product.available = !product.available;
        this.setState({
            products: this.state.products.map(item =>
                item.name === product.name ? product : item
            )
        });
    }

    handleChange(event) {
        const {name, value} = event.target;
        this.setState({[name]: value});
    }

    handleKeyUp(event) {
        if (event.key === 'Enter') {
            const {name, description, supplier} = this.state;
            this.addProduct({name, description, supplier, available: false})
        }
    }

    render() {
        const products = this.state.products.filter((product =>
                (this.state.filter === 'PENDING' && !product.available) ||
                (this.state.filter === 'available' && product.available) ||
                (this.state.filter === 'ALL')
        ))
        return (
            < div
        className = "card" >
            < div
        className = "form-group" >
            < h2 >
            Products < small > List < /small>
            < /h2>
            < div
        id = "filter" >
            < label > All < /label>
            < input
        type = "checkbox"
        name = "all"
        value = {this.state.filter}
        onChange = {()
    =>
        this.setState({filter: 'ALL'})
    }
        checked = {this.state.filter === 'ALL'}
        />
        < label > Pending < /label>
        < input
        type = "checkbox"
        name = "pending"
        value = {this.state.filter}
        onChange = {()
    =>
        this.setState({filter: 'PENDING'})
    }
        checked = {this.state.filter === 'PENDING'}
        />
        < label > available < /label>
        < input
        type = "checkbox"
        name = "available"
        value = {this.state.filter}
        onChange = {()
    =>
        this.setState({filter: 'available'})
    }
        checked = {this.state.filter === 'available'}
        />
        < /div>

        < div
        id = "names" >
            < input
        type = "text"
        className = "form-control"
        placeholder = "product name"
        name = "name"
        value = {this.state.name}
        onChange = {this.handleChange.bind(this)}
        />
        < input
        type = "text"
        className = "form-control"
        placeholder = "description"
        name = "description"
        value = {this.state.description}
        onChange = {this.handleChange.bind(this)}
        />
        < input
        type = "text"
        className = "form-control"
        placeholder = "supplier"
        name = "supplier"
        value = {this.state.supplier}
        onChange = {this.handleChange.bind(this)}
        onKeyUp = {this.handleKeyUp.bind(this)}
        />
        < /div>
        < button
        type = "button"
        className = "btn btn btn-primary"
        onClick = {this.addProduct.bind(this)} >
            Add
            < /button>
            < /div>
            < ul
        className = "list-unstyled"
        id = "product" >
            {
                products.map(product => (
                    < li key = {product.name} >
                < span onClick = {()
    =>
        this.toggleAvailable(product)
    }
        className = {
        !product.available ? " cross-out" : ""
    }>
        {
            product.name
        }
        {
            " "
        }
    <
        /span>

        < Link
        to = {"/" +product.name} > details < /Link>

            < a
        href = "#"
        className = "close"
        aria - hidden = "true"
        onClick = {()
    =>
        this.removeProduct(product)
    }>&
        times;
    <
        /a>
        < /li>
    ))
    }
    <
        /ul>
        < /div>
    )
        ;
    }
}

export default App;
