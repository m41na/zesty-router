<#import "index.ftl" as u>
<@u.page>
<#list users?values as user>
<div class="content">
    <p class="name">${user.name}</p>
    <p class="email">${user.email}</p>
    <p class="link">
        <a href="#" onclick="removeUser(event, '/users/delete/${user.id}')">delete</a>
    </p>
</div>
</#list>
<script>
    function removeUser(e, url){
        e.preventDefault();
        fetch(url, {method: 'DELETE'})
            .then(res=>console.log(res))
            .catch(err=>console.log(err));
    }
</script>
</@u.page>