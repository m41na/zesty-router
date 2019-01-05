<#import "index.ftl" as l>
<#import "user.ftl" as u>
<@l.page>
<div class="content" data-key="create">
    <form action="/users/create" onsubmit="saveUser(event, this)">
        <input type="hidden" name="id"/>
        <div class="input-row"><span class="title">Name</span><input type="text" name="name"/></div>
        <div class="input-row"><span class="title">Email</span><input type="text" name="email"/></div>
        <div class="input-row"><input class="button" type="submit" value="Save"/></div>
    </form>
</div>
<#list users?values as user>
    <@u.user user/>
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
    function saveUser(e, form){
        e.preventDefault();
        var formData = new FormData(form)
        let id = formData.get("id");
        return id? updateUser(id, formData) : createUser(formData);
    }
    function createUser(form){
        fetch('/users/create', {method: 'POST', body: encodeFormData(form), 'Content-Type': 'application/x-www-form-urlencoded'})
            .then(res=> res.text())
            .then(text=>{
                console.log(text)
            })
            .catch(err=>{
                console.log(err)
            })
    }
    function updateUser(id, form){
        fetch('/users/update/' + id, {method: 'PUT', body: encodeFormData(form), 'Content-Type': 'application/x-www-form-urlencoded'})
            .then(res=> res.text())
            .then(text=>{
                console.log(text)
            })
            .catch(err=>{
                console.log(err)
            })
    }
    function encodeFormData(data){
        var urlEncodedData = "";
        var urlEncodedDataPairs = [];
        var name;
        for(const name of data.keys()) {
            urlEncodedDataPairs.push(encodeURIComponent(name) + '=' + encodeURIComponent(data.get(name)));
        }
        return urlEncodedDataPairs.join('&').replace(/%20/g, '+');
    }
</script>
</@l.page>