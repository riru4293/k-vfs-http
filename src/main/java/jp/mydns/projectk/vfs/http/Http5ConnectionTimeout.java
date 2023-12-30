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
import jakarta.json.JsonValue;
import java.time.Duration;
import java.util.Objects;
import java.util.ServiceLoader;
import jp.mydns.projectk.vfs.AbstractFileOption;
import jp.mydns.projectk.vfs.FileOption;
import static jp.mydns.projectk.vfs.FileOptionSourceValidator.requireDuration;
import org.apache.commons.vfs2.FileSystemOptions;
import org.apache.commons.vfs2.provider.http5.Http5FileSystemConfigBuilder;

/**
 * Duration of the connection timeout.
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
 * @see Http5FileSystemConfigBuilder#getConnectionTimeoutDuration(org.apache.commons.vfs2.FileSystemOptions)
 */
@FileOption.Name("http:connectionTimeout")
public class Http5ConnectionTimeout extends AbstractFileOption {

    private final Duration value;

    /**
     * Constructor.
     *
     * @param value option value
     * @throws NullPointerException if {@code value} is {@code null}
     * @throws IllegalArgumentException if {@code value} is negative
     * @since 1.0.0
     */
    public Http5ConnectionTimeout(Duration value) {

        this(Json.createValue(Objects.requireNonNull(value).toString()));

    }

    /**
     * Constructor.
     *
     * @param value option value
     * @throws NullPointerException if {@code value} is {@code null}
     * @throws IllegalArgumentException if {@code value} is not convertible to type {@code Duration}
     * @since 1.0.0
     */
    public Http5ConnectionTimeout(JsonValue value) {

        this.value = requireDuration(Objects.requireNonNull(value), "http:connectionTimeout");

    }

    /**
     * {@inheritDoc}
     *
     * @since 1.0.0
     */
    @Override
    public JsonValue getValue() {

        return Json.createValue(value.toString());

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

        Http5FileSystemConfigBuilder.getInstance().setConnectionTimeout(opts, value);

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

        return other instanceof Http5ConnectionTimeout o
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

    /**
     * Resolver for {@link Http5ConnectionTimeout} instance from JSON.
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
