/*
 *    Copyright 2017 - 2018 BreadMoirai (Ton Ly)
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.github.breadmoirai


import org.gradle.api.Project
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import java.util.concurrent.Callable

/**
 * An extension for the {@link com.github.breadmoirai.GithubReleasePlugin}
 * <p> See Default Values below </p>
 * <p>
 *     <table>
 *         <tr>
 *             <th>Property</th>
 *             <th>Default Value</th>
 *         </tr>
 *         <tr>
 *             <td>owner</td>
 *             <td>project.group<br />
 *                 part after last period</td>
 *         </tr>
 *         <tr>
 *             <td>repo</td>
 *             <td>project.name ||<br />
 *                 rootProject.name</td>
 *         </tr>
 *         <tr>
 *             <td>tagName</td>
 *             <td>'v' + project.version</td>
 *         </tr>
 *         <tr>
 *             <td>targetCommitish</td>
 *             <td>'master'</td>
 *         </tr>
 *         <tr>
 *             <td>releaseName</td>
 *             <td>'v' + project.version</td>
 *         </tr>
 *         <tr>
 *             <td>body</td>
 *             <td>list of commits since last release</td>
 *         </tr>
 *         <tr>
 *             <td>draft</td>
 *             <td>false</td>
 *         </tr>
 *         <tr>
 *             <td>prerelease</td>
 *             <td>false</td>
 *         </tr>
 *         <tr>
 *             <td>authorization</td>
 *             <td>N/A</td>
 *         </tr>
 *         <tr>
 *             <td>overwrite</td>
 *             <td>false</td>
 *         </tr>
 *         <tr>
 *              <td>allowUploadToExisting</td>
 *              <td>false</td>
 *          </tr>
 *     </table>
 * </p>
 *
 */
class GithubReleaseExtension {

    private final Logger logger = LoggerFactory.getLogger(GithubReleasePlugin.class)

    final Property<CharSequence> owner
    final Property<CharSequence> repo
    final Property<CharSequence> authorization
    final Property<CharSequence> tagName
    final Property<CharSequence> targetCommitish
    final Property<CharSequence> releaseName
    final Property<CharSequence> body
    final Property<Boolean> draft
    final Property<Boolean> prerelease
    final ConfigurableFileCollection releaseAssets
    final Property<Boolean> overwrite
    final Property<Boolean> allowUploadToExisting
    private final Project project

    Provider<CharSequence> cachedOwner
    Provider<CharSequence> cachedRepo
    Provider<CharSequence> cachedAuthorization
    Provider<CharSequence> cachedTagName
    Provider<CharSequence> cachedTargetCommitish
    Provider<CharSequence> cachedReleaseName
    Provider<CharSequence> cachedBody
    Provider<Boolean> cachedDraft
    Provider<Boolean> cachedPrerelease
    Provider<Boolean> cachedOverwrite
    Provider<Boolean> cachedAllowUploadToExisting

    GithubReleaseExtension(Project project) {
        this.project = project
        final ObjectFactory objectFactory = project.objects
        owner = objectFactory.property(CharSequence)
        repo = objectFactory.property(CharSequence)
        authorization = objectFactory.property(CharSequence)
        tagName = objectFactory.property(CharSequence)
        targetCommitish = objectFactory.property(CharSequence)
        releaseName = objectFactory.property(CharSequence)
        body = objectFactory.property(CharSequence)
        draft = objectFactory.property(Boolean)
        prerelease = objectFactory.property(Boolean)
        releaseAssets = project.files()
        overwrite = objectFactory.property(Boolean)
        allowUploadToExisting = objectFactory.property(Boolean)
    }

    Callable<String> changelog(@DelegatesTo(ChangeLogSupplier) final Closure closure) {
        def c = new ChangeLogSupplier(this, project.objects)
        c.with closure
        return c
    }

    Callable<String> changelog() {
        return new ChangeLogSupplier(this, project.objects)
    }

    Provider<CharSequence> getOwnerProvider() {
        if (cachedOwner == null) {
            cachedOwner = new CachedProvider<>(owner)
            if (logger.isDebugEnabled())
                cachedOwner = new DebugProvider<>(logger, "owner", cachedOwner)
        }
        return cachedOwner
    }

    Provider<CharSequence> getRepoProvider() {
        if (cachedRepo == null) {
            cachedRepo = new CachedProvider<>(repo)
            if (logger.isDebugEnabled())
                cachedRepo = new DebugProvider(logger, "repo", cachedRepo)
        }
        return cachedRepo
    }

    Provider<CharSequence> getAuthorizationProvider() {
        if (cachedAuthorization == null) {
            cachedAuthorization = new CachedProvider<>(authorization)
            if (logger.isDebugEnabled())
                return new DebugProvider(logger, "authorization", cachedAuthorization)
        }
        return cachedAuthorization
    }

    Provider<CharSequence> getTagNameProvider() {
        if (cachedTagName == null) {
            cachedTagName = new CachedProvider<>(tagName)
            if (logger.isDebugEnabled())
                cachedTagName = new DebugProvider(logger, "tagName", cachedTagName)
        }
        return cachedTagName
    }

    Provider<CharSequence> getTargetCommitishProvider() {
        if (cachedTargetCommitish == null) {
            cachedTargetCommitish = new CachedProvider<>(targetCommitish)
            if (logger.isDebugEnabled())
                return new DebugProvider(logger, "=", cachedTargetCommitish)
            return cachedTargetCommitish
        }
    }

    Provider<CharSequence> getReleaseNameProvider() {
        if (cachedReleaseName == null) {
            cachedReleaseName = new CachedProvider<>(releaseName)
            if (logger.isDebugEnabled())
                cachedReleaseName = new DebugProvider(logger, "releaseName", cachedReleaseName)
        }
        return cachedReleaseName
    }

    Provider<CharSequence> getBodyProvider() {
        if (cachedBody == null) {
            cachedBody = new CachedProvider<>(body)
            if (logger.isDebugEnabled())
                cachedBody = new DebugProvider(logger, "body", cachedBody)
        }
        return cachedBody
    }

    Provider<Boolean> getDraftProvider() {
        if (cachedDraft == null) {
            cachedDraft = new CachedProvider<>(draft)
            if (logger.isDebugEnabled())
                cachedDraft = new DebugProvider(logger, "draft", cachedDraft)
        }
        return cachedDraft
    }

    Provider<Boolean> getPrereleaseProvider() {
        if (cachedPrerelease == null) {
            cachedPrerelease = new CachedProvider<>(prerelease)
            if (logger.isDebugEnabled())
                cachedPrerelease = new DebugProvider(logger, "prerelease", cachedPrerelease)
        }
        return cachedPrerelease
    }

    ConfigurableFileCollection getReleaseAssets() {
        return releaseAssets
    }

    Provider<Boolean> getOverwriteProvider() {
        if (cachedOverwrite == null) {
            cachedOverwrite = new CachedProvider<>(overwrite)
            if (logger.isDebugEnabled())
                cachedOverwrite = new DebugProvider(logger, "overwrite", cachedOverwrite)
        }
        return cachedOverwrite
    }

    Provider<Boolean> getAllowUploadToExistingProvider() {
        if (cachedAllowUploadToExisting == null) {
            cachedAllowUploadToExisting = new CachedProvider<>(allowUploadToExisting)
            if (logger.isDebugEnabled())
                cachedAllowUploadToExisting = new DebugProvider(logger, "allowUploadToExisting", cachedAllowUploadToExisting)
        }
        return cachedAllowUploadToExisting
    }

    void setOwner(CharSequence owner) {
        this.owner.set(owner)
    }

    void owner(CharSequence owner) {
        this.owner.set(owner)
    }

    void setOwner(Provider<? extends CharSequence> owner) {
        this.owner.set(owner)
    }

    void owner(Provider<? extends CharSequence> owner) {
        this.owner.set(owner)
    }

    void setOwner(Callable<? extends CharSequence> owner) {
        this.owner.set(new TypedDefaultProvider<>(CharSequence.class, owner))
    }

    void owner(Callable<? extends CharSequence> owner) {
        this.owner.set(new TypedDefaultProvider<>(CharSequence.class, owner))
    }

    void setRepo(CharSequence repo) {
        this.repo.set(repo)
    }

    void repo(CharSequence repo) {
        this.repo.set(repo)
    }

    void setRepo(Provider<? extends CharSequence> repo) {
        this.repo.set(repo)
    }

    void repo(Provider<? extends CharSequence> repo) {
        this.repo.set(repo)
    }

    void setRepo(Callable<? extends CharSequence> repo) {
        this.repo.set(new TypedDefaultProvider<>(CharSequence.class, repo))
    }

    void repo(Callable<? extends CharSequence> repo) {
        this.repo.set(new TypedDefaultProvider<>(CharSequence.class, repo))
    }

    void setToken(CharSequence token) {
        this.authorization.set("Token $token")
    }

    void token(CharSequence token) {
        this.authorization.set("Token $token")
    }

    void setToken(Provider<? extends CharSequence> token) {
        this.authorization.set(token.map { "Token $it" })
    }

    void token(Provider<? extends CharSequence> token) {
        this.authorization.set(token.map { "Token $it" })
    }

    void setToken(Callable<? extends CharSequence> token) {
        this.authorization.set(new TypedDefaultProvider(CharSequence.class, token).map { "Token $it" })
    }

    void token(Callable<? extends CharSequence> token) {
        this.authorization.set(new TypedDefaultProvider(CharSequence.class, token).map { "Token $it" })
    }

    void setAuthorization(CharSequence authorization) {
        this.authorization.set(authorization)
    }

    void authorization(CharSequence authorization) {
        this.authorization.set(authorization)
    }

    void setAuthorization(Provider<? extends CharSequence> authorization) {
        this.authorization.set(authorization)
    }

    void authorization(Provider<? extends CharSequence> authorization) {
        this.authorization.set(authorization)
    }

    void setAuthorization(Callable<? extends CharSequence> authorization) {
        this.authorization.set(new TypedDefaultProvider<>(CharSequence.class, authorization))
    }

    void authorization(Callable<? extends CharSequence> authorization) {
        this.authorization.set(new TypedDefaultProvider<>(CharSequence.class, authorization))
    }

    void setTagName(CharSequence tagName) {
        this.tagName.set(tagName)
    }

    void tagName(CharSequence tagName) {
        this.tagName.set(tagName)
    }

    void setTagName(Provider<? extends CharSequence> tagName) {
        this.tagName.set(tagName)
    }

    void tagName(Provider<? extends CharSequence> tagName) {
        this.tagName.set(tagName)
    }

    void setTagName(Callable<? extends CharSequence> tagName) {
        this.tagName.set(new TypedDefaultProvider<>(CharSequence.class, tagName))
    }

    void tagName(Callable<? extends CharSequence> tagName) {
        this.tagName.set(new TypedDefaultProvider<>(CharSequence.class, tagName))
    }

    void setTargetCommitish(CharSequence targetCommitish) {
        this.targetCommitish.set(targetCommitish)
    }

    void targetCommitish(CharSequence targetCommitish) {
        this.targetCommitish.set(targetCommitish)
    }

    void setTargetCommitish(Provider<? extends CharSequence> targetCommitish) {
        this.targetCommitish.set(targetCommitish)
    }

    void targetCommitish(Provider<? extends CharSequence> targetCommitish) {
        this.targetCommitish.set(targetCommitish)
    }

    void setTargetCommitish(Callable<? extends CharSequence> targetCommitish) {
        this.targetCommitish.set(new TypedDefaultProvider<>(CharSequence.class, targetCommitish))
    }

    void targetCommitish(Callable<? extends CharSequence> targetCommitish) {
        this.targetCommitish.set(new TypedDefaultProvider<>(CharSequence.class, targetCommitish))
    }

    void setReleaseName(CharSequence releaseName) {
        this.releaseName.set(releaseName)
    }

    void releaseName(CharSequence releaseName) {
        this.releaseName.set(releaseName)
    }

    void setReleaseName(Provider<? extends CharSequence> releaseName) {
        this.releaseName.set(releaseName)
    }

    void releaseName(Provider<? extends CharSequence> releaseName) {
        this.releaseName.set(releaseName)
    }

    void setReleaseName(Callable<? extends CharSequence> releaseName) {
        this.releaseName.set(new TypedDefaultProvider<>(CharSequence.class, releaseName))
    }

    void releaseName(Callable<? extends CharSequence> releaseName) {
        this.releaseName.set(new TypedDefaultProvider<>(CharSequence.class, releaseName))
    }

    void setBody(CharSequence body) {
        this.body.set(body)
    }

    void body(CharSequence body) {
        this.body.set(body)
    }

    void setBody(Provider<? extends CharSequence> body) {
        this.body.set(body)
    }

    void body(Provider<? extends CharSequence> body) {
        this.body.set(body)
    }

    void setBody(Callable<? extends CharSequence> body) {
        this.body.set(new TypedDefaultProvider<>(CharSequence.class, body))
    }

    void body(Callable<? extends CharSequence> body) {
        this.body.set(new TypedDefaultProvider<>(CharSequence.class, body))
    }

    void setDraft(boolean draft) {
        this.draft.set(draft)
    }

    void draft(boolean draft) {
        this.draft.set(draft)
    }

    void setDraft(Provider<? extends Boolean> draft) {
        this.draft.set(draft)
    }

    void draft(Provider<? extends Boolean> draft) {
        this.draft.set(draft)
    }

    void setDraft(Callable<? extends Boolean> draft) {
        this.draft.set(new TypedDefaultProvider<>(Boolean.class, draft))
    }

    void draft(Callable<? extends Boolean> draft) {
        this.draft.set(new TypedDefaultProvider<>(Boolean.class, draft))
    }

    void setPrerelease(boolean prerelease) {
        this.prerelease.set(prerelease)
    }

    void prerelease(boolean prerelease) {
        this.prerelease.set(prerelease)
    }

    void setPrerelease(Provider<? extends Boolean> prerelease) {
        this.prerelease.set(prerelease)
    }

    void prerelease(Provider<? extends Boolean> prerelease) {
        this.prerelease.set(prerelease)
    }

    void setPrerelease(Callable<? extends Boolean> prerelease) {
        this.prerelease.set(new TypedDefaultProvider<>(Boolean.class, prerelease))
    }

    void prerelease(Callable<? extends Boolean> prerelease) {
        this.prerelease.set(new TypedDefaultProvider<>(Boolean.class, prerelease))
    }

    void setReleaseAssets(Object... assets) {
        this.releaseAssets.setFrom(assets)
    }

    void releaseAssets(Object... assets) {
        this.releaseAssets.setFrom(assets)
    }

    void setOverwrite(Boolean replacePrevious) {
        this.overwrite.set(replacePrevious)
    }

    void overwrite(Boolean replacePrevious) {
        this.overwrite.set(replacePrevious)
    }

    void setOverwrite(Provider<Boolean> replacePrevious) {
        this.overwrite.set(replacePrevious)
    }

    void overwrite(Provider<Boolean> replacePrevious) {
        this.overwrite.set(replacePrevious)
    }

    void setOverwrite(Callable<Boolean> replacePrevious) {
        this.overwrite.set(new TypedDefaultProvider<>(Boolean.class, replacePrevious))
    }

    void overwrite(Callable<Boolean> replacePrevious) {
        this.overwrite.set(new TypedDefaultProvider<>(Boolean.class, replacePrevious))
    }

    void setAllowUploadToExisting(Boolean allowUploadToExisting) {
        this.allowUploadToExisting.set(allowUploadToExisting)
    }

    void allowUploadToExisting(Boolean allowUploadToExisting) {
        this.allowUploadToExisting.set(allowUploadToExisting)
    }

    void setAllowUploadToExisting(Provider<Boolean> allowUploadToExisting) {
        this.allowUploadToExisting.set(allowUploadToExisting)
    }

    void allowUploadToExisting(Provider<Boolean> allowUploadToExisting) {
        this.allowUploadToExisting.set(allowUploadToExisting)
    }

    void setAllowUploadToExisting(Callable<Boolean> allowUploadToExisting) {
        this.allowUploadToExisting.set(new TypedDefaultProvider<>(Boolean.class, allowUploadToExisting))
    }

    void allowUploadToExisting(Callable<Boolean> allowUploadToExisting) {
        this.allowUploadToExisting.set(new TypedDefaultProvider<>(Boolean.class, allowUploadToExisting))
    }

}
