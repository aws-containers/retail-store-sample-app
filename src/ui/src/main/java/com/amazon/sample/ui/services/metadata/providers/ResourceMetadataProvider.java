/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: MIT-0
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this
 * software and associated documentation files (the "Software"), to deal in the Software
 * without restriction, including without limitation the rights to use, copy, modify,
 * merge, publish, distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.amazon.sample.ui.services.metadata.providers;

import com.amazon.sample.ui.services.metadata.MetadataAttribute;
import com.amazon.sample.ui.services.metadata.MetadataProvider;
import com.amazon.sample.ui.services.metadata.MetadataSet;
import io.opentelemetry.sdk.resources.Resource;

public abstract class ResourceMetadataProvider implements MetadataProvider {

  private String name;

  public ResourceMetadataProvider(String name) {
    this.name = name;
  }

  public MetadataSet get() {
    var resource = this.getResource();

    if (resource.getAttributes().isEmpty()) {
      return null;
    }

    MetadataSet set = new MetadataSet(this.name);
    resource
      .getAttributes()
      .forEach((key, value) -> {
        if (value instanceof String) {
          set.add(
            new MetadataAttribute(key.getKey(), value.toString(), this.name)
          );
        }
      });

    return set;
  }

  abstract Resource getResource();
}
