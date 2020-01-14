import {Route, Switch} from 'react-router-dom'
import Products from './components/Products'
import Details from './components/Details'

export default function App({products}) {
    return (
        < Switch >
        < Route
    path = "/"
    exact
    render = {()
=> <
    Products
    products = {products}
    />}/ >
    < Route
    path = "/:product"
    exact
    render = {({match})
=> <
    Details
    product = {products.find(item => item.name == match.params.product)}
    />}/ >
    < /Switch>
)
}
