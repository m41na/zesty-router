import {Link} from 'react-router-dom';

export default function Details({product}){

    return (
        <div>
            <p>{product.name}</p>
            <p>{product.description}</p>
            <p><Link to={"/"}>back</Link></p>
        </div>
    )
}