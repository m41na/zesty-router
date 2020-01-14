<#import "index.ftl" as l>
<@l.page>
    <div class="content" data-key="edit">
        <form action="/users/create" onsubmit="saveUser(event, this)">
            <input type="hidden" name="id"/>
            <div class="input-row"><span class="title">Name</span><input type="text" name="name"/></div>
            <div class="input-row"><span class="title">Email</span><input type="text" name="email"/></div>
            <div class="input-row"><input class="button" type="submit" value="Save"/></div>
        </form>
    </div>
    <#list users?values as user>
        <#include "user.ftl"/>
    </#list>
    <script>
        function removeUser(e, id) {
            e.preventDefault();
            fetch('/users/delete/' + id, {method: 'DELETE'})
                .then(res => {
                    let user = document.querySelector("[data-key='" + id + "']");
                    user.remove();
                })
                .catch(err => console.log(err));
        }

        function saveUser(e, form) {
            e.preventDefault();
            var formData = new FormData(form)
            let id = formData.get("id");
            return id ? updateUser(id, formData) : createUser(formData);
        }

        function createUser(form) {
            const options = {
                method: 'post',
                headers: {
                    'Content-type': 'application/x-www-form-urlencoded; charset=UTF-8'
                },
                body: encodeFormData(form)
            }
            fetch('/users/create', options)
                .then(res => res.text())
                .then(html => {
                    let parent = document.getElementById("wrapper");
                    let element = htmlToElement(html);
                    parent.appendChild(element);
                })
                .catch(err => {
                    console.log(err)
                })
        }

        function updateUser(id, form) {
            const options = {
                method: 'put',
                headers: {
                    'Content-type': 'application/x-www-form-urlencoded; charset=UTF-8'
                },
                body: encodeFormData(form)
            }
            fetch('/users/update/' + id, options)
                .then(res => res.text())
                .then(html => {
                    let parent = document.getElementById("wrapper");
                    let element = htmlToElement(html);
                    let target = parent.querySelector("[data-key='" + id + "']");
                    parent.replaceChild(element, target);
                    this.resetForm();
                })
                .catch(err => {
                    console.log(err)
                })
        }

        function selectUser(e, id, name, email) {
            e.preventDefault();
            let form = document.querySelector("[data-key='edit'] form");
            form.elements["id"].value = id;
            form.elements["name"].value = name;
            form.elements["email"].value = email;
        }

        function resetForm() {
            let form = document.querySelector("[data-key='edit'] form");
            form.elements["id"].value = "";
            form.elements["name"].value = "";
            form.elements["email"].value = "";
        }

        function encodeFormData(data) {
            var urlEncodedData = "";
            var urlEncodedDataPairs = [];
            var name;
            for (const name of data.keys()) {
                urlEncodedDataPairs.push(encodeURIComponent(name) + '=' + encodeURIComponent(data.get(name)));
            }
            return urlEncodedDataPairs.join('&').replace(/%20/g, '+');
        }

        function htmlToElement(html) {
            var template = document.createElement('template');
            template.innerHTML = html.trim();
            return template.content.firstChild;
        }
    </script>
</@l.page>
