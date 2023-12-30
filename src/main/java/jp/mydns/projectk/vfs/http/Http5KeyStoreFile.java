/*
 * Copyright (c) 2023, Project-K
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package jp.mydns.projectk.vfs.http;

import jakarta.json.Json;
import jakarta.json.JsonString;
import jakarta.json.JsonValue;
import java.net.URI;
import java.nio.file.FileSystemNotFoundException;
import java.nio.file.Path;
import java.util.Objects;
import java.util.ServiceLoader;
import jp.mydns.projectk.vfs.AbstractFileOption;
import jp.mydns.projectk.vfs.FileOption;
import org.apache.commons.vfs2.FileSystemOptions;
import org.apache.commons.vfs2.provider.http5.Http5FileSystemConfigBuilder;

/**
 * Keystore file path as {@code URI} to be used in TLS connections. Must be file schema and absolute URI.
 * <p>
 * Implementation requirements.
 * <ul>
 * <li>This class is immutable and thread-safe.</li>
 * <li>This class and JSON can be converted bidirectionally.</li>
 * <li>Can reflect this class on the {@link FileSystemOptions}.</li>
 * </ul>
 *
 * @author riru
 * @version 1.0.0
 * @since 1.0.0
 * @see Http5FileSystemConfigBuilder#getKeyStoreFile(org.apache.commons.vfs2.FileSystemOptions)
 */
@FileOption.Name("http:keyStoreFileUri")
public class Http5KeyStoreFile extends AbstractFileOption {

    private final Path value;

    /**
     * Constructor.
     *
     * @param value option value
     * @throws NullPointerException if {@code value} is {@code null}
     * @throws IllegalArgumentException if {@code value} is not absolute path
     * @since 1.0.0
     */
    public Http5KeyStoreFile(Path value) {

        this.value = requireAbsolute(Objects.requireNonNull(value));

    }

    /**
     * Constructor.
     *
     * @param value option value
     * @throws NullPointerException if {@code value} is {@code null}
     * @throws IllegalArgumentException if {@code value} is not convertible to type {@code Path} via {@code URI}, or if
     * {@code value} is not absolute path
     * @since 1.0.0
     */
    public Http5KeyStoreFile(JsonValue value) {

        this.value = requirePathViaUri(Objects.requireNonNull(value));

    }

    /**
     * {@inheritDoc}
     *
     * @since 1.0.0
     */
    @Override
    public JsonValue getValue() {

        return Json.createValue(value.toUri().toString());

    }

    /**
     * {@inheritDoc}
     *
     * @param opts the {@code FileSystemOptions}. This value will be modified.
     * @throws NullPointerException if {@code opts} is {@code null}
     * @since 1.0.0
     */
    @Override
    public void apply(FileSystemOptions opts) {

        Objects.requireNonNull(opts);

        Http5FileSystemConfigBuilder.getInstance().setKeyStoreFile(opts, value.toString());

    }

    /**
     * Returns a hash code value.
     *
     * @return a hash code value
     * @since 1.0.0
     */
    @Override
    public int hashCode() {

        return Objects.hash(getName(), getValue());

    }

    /**
     * Indicates that other object is equal to this one.
     *
     * @param other an any object
     * @return {@code true} if equals, otherwise {@code false}.
     * @since 1.0.0
     */
    @Override
    public boolean equals(Object other) {

        return other instanceof Http5KeyStoreFile o
                && Objects.equals(getName(), o.getName())
                && Objects.equals(getValue(), o.getValue());

    }

    /**
     * Returns a string representation of this.
     *
     * @return string representation
     * @since 1.0.0
     */
    @Override
    public String toString() {

        return Json.createObjectBuilder().add(getName(), getValue()).build().toString();

    }

    private Path requirePathViaUri(JsonValue value) {

        try {

            return requireAbsolute(Path.of(URI.create(JsonString.class.cast(value).getString())));

        } catch (ClassCastException | IllegalArgumentException | FileSystemNotFoundException ex) {

            throw new IllegalArgumentException("FileOption value of [http:keyStoreFileUri] must be convertible to URI,"
                    + " and further convertible to absolute path of local file.", ex);

        }

    }

    private Path requireAbsolute(Path value) {

        if (!value.isAbsolute()) {

            throw new IllegalArgumentException("Must be absolute path.");

        }

        return value;

    }

    /**
     * Resolver for {@link Http5KeyStoreFile} instance from JSON.
     * <p>
     * Implementation requirements.
     * <ul>
     * <li>This class is immutable and thread-safe.</li>
     * <li>Implementations of this interface must be able to construct instances using {@link ServiceLoader}.</li>
     * <li>This class must be able to construct an instance of {@code FileOption} from the JSON representing
     * {@code FileOption}.</li>
     * </ul>
     *
     * @author riru
     * @version 1.0.0
     * @since 1.0.0
     */
    public static class Resolver implements FileOption.Resolver {
    }
}
