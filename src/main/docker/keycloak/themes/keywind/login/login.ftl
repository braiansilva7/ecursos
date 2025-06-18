<#import "template.ftl" as layout>
<@layout.registrationLayout displayInfo=social.displayInfo; section>
    <#if section = "title">
        ${msg("loginTitle",(realm.displayName!''))}
    <#elseif section = "header">
	<div class="title">
        <img src="${url.resourcesPath}/img/gladio-preta.png" alt="Logo" style="width: 137px; margin-right: 10px;">
    </div>
    <div class="title-fab" style="font-size: 37px; bottom: 10px; font-weight: bold;">FORÇA AÉREA BRASILEIRA</div>
    <div class="title-unico">Login Único</div>
    <#elseif section = "form">
        <img src="${url.resourcesPath}/img/ecurso.png" alt="Logo" style="width: 300px; margin-right: 10px;">
        <#if realm.password>
            <form id="kc-form-login" class="form ${properties.kcFormClass!}" action="${url.loginAction}" method="post">
                <div class="username-container ${properties.kcFormGroupClass!}">
                    <div class="${properties.kcLabelWrapperClass!}">
                        <label for="username" class="${properties.kcLabelClass!}"><#if !realm.loginWithEmailAllowed>${msg("username")}<#elseif !realm.registrationEmailAsUsername>${msg("usernameOrEmail")}<#else>${msg("email")}</#if></label>
                    </div>

                    <div class="${properties.kcInputWrapperClass!}">
                        <#if usernameEditDisabled??>
                            <input id="username" class="form-control ${properties.kcInputClass!}" name="username" value="${(login.username!'')}" type="text" disabled />
                        <#else>
                            <input id="username" class="form-control ${properties.kcInputClass!}" name="username" value="${(login.username!'')}" type="text" autofocus autocomplete="off" />
                        </#if>
                    </div>
                </div>
                <div class="password-container ${properties.kcFormGroupClass!}">
                    <div class="${properties.kcLabelWrapperClass!}">
                        <label for="password" class="${properties.kcLabelClass!}">${msg("password")}</label>
                    </div>

                    <div class="${properties.kcInputWrapperClass!}">
                        <input id="password" class="form-control ${properties.kcInputClass!}" name="password" type="password" autocomplete="off" />
                    </div>
                </div>

                <div class="${properties.kcFormGroupClass!} row">
                    <div id="kc-form-options" class="${properties.kcFormOptionsClass!}">
                        <div class="col-xs-7">
                            <#if realm.rememberMe && !usernameEditDisabled??>
                                <div class="checkbox remember-me-checkbox">
                                    <label>
                                        <#if login.rememberMe??>
                                            <input id="rememberMe" name="rememberMe" type="checkbox" tabindex="3" checked>&nbsp;&nbsp;${msg("rememberMe")}
                                        <#else>
                                            <input id="rememberMe" name="rememberMe" type="checkbox" tabindex="3">&nbsp;&nbsp;${msg("rememberMe")}
                                        </#if>
                                    </label>
                                </div>
                            </#if>
                        </div>
                        <div id="kc-form-buttons" class="col-xs-5 ${properties.kcFormButtonsClass!}">
                            <div class="${properties.kcFormButtonsWrapperClass!}">
                                <input class="btn btn-primary btn-block ${properties.kcButtonClass!} ${properties.kcButtonPrimaryClass!} ${properties.kcButtonLargeClass!}" name="login" id="kc-login" type="submit" value="${msg("doLogIn")}"/>
                            </div>
                            <div class="clearfix"></div>
                        </div>
                    </div>
                    <#if (realm.password && realm.registrationAllowed && !usernameEditDisabled??) || realm.resetPasswordAllowed>
                        <div>
                            <div class="col-xs-12">
                                <hr class="separator" />
                            </div>
                        </div>
                        <div>
                            <#if realm.password && realm.registrationAllowed && !usernameEditDisabled??>
                                <div id="kc-registration" class="col-xs-12">
                                    <span>${msg("noAccount")} <a href="${url.registrationUrl}">${msg("doRegister")}</a></span>
                                </div>
                            </#if>
                        </div>
                        <div>
                            <div class="${properties.kcFormOptionsWrapperClass!} col-xs-12">
                                <#if realm.resetPasswordAllowed>
                                    <span><a href="${url.loginResetCredentialsUrl}">${msg("doForgotPassword")}</a></span>
                                </#if>
                            </div>
                        </div>
                    </#if>
                </div>
            </form>
        </#if>
    <#elseif section = "info" >
        <#if realm.password && social.providers??>
            <div id="kc-social-providers">
                <ul>
                    <#list social.providers as p>
                        <li><a href="${p.loginUrl}" id="zocial-${p.alias}" class="zocial ${p.providerId}"> <span class="text">${p.displayName}</span></a></li>
                    </#list>
                </ul>
            </div>
        </#if>
    </#if>    
</@layout.registrationLayout>
<footer class="site-footer">
        <div class="footer-item">
            <img class="footer-logo gladio-sti" src="${url.resourcesPath}/img/sti.png" alt="Logo STI" />
            <span class="footer-text">STI - Força Aérea Brasileira</span>
        </div>
        <div class="footer-item">
            <img class="footer-logo gladio-ccasj" src="${url.resourcesPath}/img/logo-ccabr.png" alt="Logo CCA-BR" />
            <span class="footer-text">
                Desenvolvido e mantido por CCA-BR
            </span>
        </div>
    </footer>