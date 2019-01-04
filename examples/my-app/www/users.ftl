<#import "index.ftl" as u>
<@u.page>
<#list users?values as user>
<div class="content" data-key="${user.id}">
    <p class="name">${user.name}</p>
    <p class="email">${user.email}</p>
    <p class="link">
        <a href="#" onclick="removeUser(event, '${user.id}')">delete</a>
    </p>
</div>
</#list>
<script>
    function removeUser(e, id){
        e.preventDefault();
        fetch('/users/delete/' + id, {method: 'DELETE'})
            .then(res=> {
                let user = document.querySelector("[data-key='" + id + "']");
                user.remove();
            })
            .catch(err=>console.log(err));
    }
</script>
</@u.page>