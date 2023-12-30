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
import java.util.List;
import java.util.Objects;
import java.util.ServiceLoader;
import static java.util.stream.Collectors.joining;
import java.util.stream.Stream;
import jp.mydns.projectk.vfs.AbstractFileOption;
import jp.mydns.projectk.vfs.FileOption;
import org.apache.commons.vfs2.FileSystemOptions;
import org.apache.commons.vfs2.provider.http5.Http5FileSystemConfigBuilder;
import org.apache.hc.core5.http.ssl.TLS;

/**
 * Enabled TLS versions as a comma separated string, each token of which is the name of
 * {@link org.apache.hc.core5.http.ssl.TLS} enum.
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
 * @see Http5FileSystemConfigBuilder#getTlsVersions(org.apache.commons.vfs2.FileSystemOptions)
 */
@FileOption.Name("http:tlsVersions")
public class Http5TlsVersions extends AbstractFileOption {

    private final List<TLS> values;

    /**
     * Constructor.
     *
     * @param values option values
     * @throws NullPointerException if {@code values} is {@code null}
     * @throws IllegalArgumentException if {@code values} is not convertible to type {@code Path} via {@code URI}
     * @since 1.0.0
     */
    public Http5TlsVersions(List<String> values) {

        this.values = requireTlsVersions(Objects.requireNonNull(values));

    }

    /**
     * Constructor.
     *
     * @param values option values
     * @throws NullPointerException if {@code values} is {@code null}
     * @throws IllegalArgumentException if {@code values} is not convertible to type {@code Path} via {@code URI}
     * @since 1.0.0
     */
    public Http5TlsVersions(JsonValue values) {

        this.values = requireTlsVersions(Objects.requireNonNull(values));

    }

    /**
     * {@inheritDoc}
     *
     * @since 1.0.0
     */
    @Override
    public JsonValue getValue() {

        return Json.createValue(values.stream().map(Enum::name).collect(joining(",")));

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

        Http5FileSystemConfigBuilder.getInstance()
                .setTlsVersions(opts, values.stream().map(Enum::name).collect(joining(",")));

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

        return other instanceof Http5TlsVersions o
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

    private List<TLS> requireTlsVersions(JsonValue values) {

        try {

            return values.asJsonArray().stream().map(JsonString.class::cast).map(JsonString::getString)
                    .map(TLS::valueOf).toList();

        } catch (ClassCastException | IllegalArgumentException ex) {

            throw new IllegalArgumentException("FileOption value of [http:tlsVersions] must be either [%s]."
                    .formatted(Stream.of(TLS.values()).map(Enum::name).collect(joining(", "))), ex);

        }

    }

    private List<TLS> requireTlsVersions(List<String> values) {

        return requireTlsVersions(Json.createArrayBuilder(values).build());

    }

    /**
     * Resolver for {@link Http5TlsVersions} instance from JSON.
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
