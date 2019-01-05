<#macro user user>
<div class="content" data-key="${user.id}">
    <p class="name">${user.name}</p>
    <p class="email">${user.email}</p>
    <p class="link">
        <a href="#" onclick="removeUser(event, '${user.id}')">delete</a>
    </p>
</div>
</#macro>