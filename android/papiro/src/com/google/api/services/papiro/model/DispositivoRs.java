/*
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
/*
 * This code was generated by https://code.google.com/p/google-apis-client-generator/
 * (build: 2013-05-23 17:46:09 UTC)
 * on 2013-06-04 at 16:04:01 UTC 
 * Modify at your own risk.
 */

package com.google.api.services.papiro.model;

/**
 * Model definition for DispositivoRs.
 *
 * <p> This is the Java data model class that specifies how to parse/serialize into the JSON that is
 * transmitted over HTTP when working with the . For a detailed explanation see:
 * <a href="http://code.google.com/p/google-http-java-client/wiki/JSON">http://code.google.com/p/google-http-java-client/wiki/JSON</a>
 * </p>
 *
 * @author Google, Inc.
 */
@SuppressWarnings("javadoc")
public final class DispositivoRs extends com.google.api.client.json.GenericJson {

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.util.List<AnuncioRs> anuncios;

  static {
    // hack to force ProGuard to consider AnuncioRs used, since otherwise it would be stripped out
    // see http://code.google.com/p/google-api-java-client/issues/detail?id=528
    com.google.api.client.util.Data.nullOf(AnuncioRs.class);
  }

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String extension;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String id;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String logo;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String ultimaActualizacion;

  /**
   * @return value or {@code null} for none
   */
  public java.util.List<AnuncioRs> getAnuncios() {
    return anuncios;
  }

  /**
   * @param anuncios anuncios or {@code null} for none
   */
  public DispositivoRs setAnuncios(java.util.List<AnuncioRs> anuncios) {
    this.anuncios = anuncios;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getExtension() {
    return extension;
  }

  /**
   * @param extension extension or {@code null} for none
   */
  public DispositivoRs setExtension(java.lang.String extension) {
    this.extension = extension;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getId() {
    return id;
  }

  /**
   * @param id id or {@code null} for none
   */
  public DispositivoRs setId(java.lang.String id) {
    this.id = id;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getLogo() {
    return logo;
  }

  /**
   * @param logo logo or {@code null} for none
   */
  public DispositivoRs setLogo(java.lang.String logo) {
    this.logo = logo;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getUltimaActualizacion() {
    return ultimaActualizacion;
  }

  /**
   * @param ultimaActualizacion ultimaActualizacion or {@code null} for none
   */
  public DispositivoRs setUltimaActualizacion(java.lang.String ultimaActualizacion) {
    this.ultimaActualizacion = ultimaActualizacion;
    return this;
  }

  @Override
  public DispositivoRs set(String fieldName, Object value) {
    return (DispositivoRs) super.set(fieldName, value);
  }

  @Override
  public DispositivoRs clone() {
    return (DispositivoRs) super.clone();
  }

}