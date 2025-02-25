{{/* vim: set filetype=mustache: */}}
{{/*
Expand the name of the chart.
*/}}
{{- define "orders.name" -}}
{{- default "orders" .Values.nameOverride | trunc 63 | trimSuffix "-" }}
{{- end }}

{{/*
Create a default fully qualified app name.
We truncate at 63 chars because some Kubernetes name fields are limited to this (by the DNS naming spec).
If release name contains chart name it will be used as a full name.
*/}}
{{- define "orders.fullname" -}}
{{- if .Values.fullnameOverride }}
{{- .Values.fullnameOverride | trunc 63 | trimSuffix "-" }}
{{- else }}
{{- $name := default "orders" .Values.nameOverride }}
{{- if contains $name .Release.Name }}
{{- .Release.Name | trunc 63 | trimSuffix "-" }}
{{- else }}
{{- printf "%s-%s" .Release.Name $name | trunc 63 | trimSuffix "-" }}
{{- end }}
{{- end }}
{{- end }}

{{/*
Create chart name and version as used by the chart label.
*/}}
{{- define "orders.chart" -}}
{{- printf "%s-%s" "orders" .Chart.Version | replace "+" "_" | trunc 63 | trimSuffix "-" }}
{{- end }}

{{/*
Common labels
*/}}
{{- define "orders.labels" -}}
helm.sh/chart: {{ include "orders.chart" . }}
{{ include "orders.selectorLabels" . }}
{{- if .Chart.AppVersion }}
app.kubernetes.io/version: {{ .Chart.AppVersion | quote }}
{{- end }}
app.kubernetes.io/managed-by: {{ .Release.Service }}
{{- end }}

{{/*
Selector labels
*/}}
{{- define "orders.selectorLabels" -}}
app.kubernetes.io/name: {{ include "orders.name" . }}
app.kubernetes.io/instance: {{ .Release.Name }}
app.kubernetes.io/component: service
app.kubernetes.io/owner: retail-store-sample
{{- end }}

{{/*
Create the name of the service account to use
*/}}
{{- define "orders.serviceAccountName" -}}
{{- if .Values.serviceAccount.create }}
{{- default (include "orders.fullname" .) .Values.serviceAccount.name }}
{{- else }}
{{- default "default" .Values.serviceAccount.name }}
{{- end }}
{{- end }}

{{/*
Create the name of the config map to use
*/}}
{{- define "orders.configMapName" -}}
{{- if .Values.configMap.create }}
{{- default (include "orders.fullname" .) .Values.configMap.name }}
{{- else }}
{{- default "default" .Values.configMap.name }}
{{- end }}
{{- end }}


{{/* podAnnotations */}}
{{- define "orders.podAnnotations" -}}
{{- if or .Values.metrics.enabled .Values.podAnnotations }}
{{- $podAnnotations := .Values.podAnnotations}}
{{- $metricsAnnotations := .Values.metrics.podAnnotations}}
{{- $allAnnotations := merge $podAnnotations $metricsAnnotations }}
{{- toYaml $allAnnotations }}
{{- end }}
{{- end -}}

{{- define "orders.postgresql.fullname" -}}
{{- include "orders.fullname" . }}-postgresql
{{- end -}}

{{/*
Common labels for postgresql
*/}}
{{- define "orders.postgresql.labels" -}}
helm.sh/chart: {{ include "orders.chart" . }}
{{ include "orders.postgresql.selectorLabels" . }}
{{- if .Chart.AppVersion }}
app.kubernetes.io/version: {{ .Chart.AppVersion | quote }}
{{- end }}
app.kubernetes.io/managed-by: {{ .Release.Service }}
{{- end }}

{{/*
Selector labels for postgresql
*/}}
{{- define "orders.postgresql.selectorLabels" -}}
app.kubernetes.io/name: {{ include "orders.fullname" . }}
app.kubernetes.io/instance: {{ .Release.Name }}
app.kubernetes.io/component: postgresql
app.kubernetes.io/owner: retail-store-sample
{{- end }}

{{- define "getOrGeneratePass" }}
{{- $len := (default 16 .Length) | int -}}
{{- $obj := (lookup "v1" .Kind .Namespace .Name).data -}}
{{- if $obj }}
{{- index $obj .Key -}}
{{- else if (eq (lower .Kind) "secret") -}}
{{- randAlphaNum $len | b64enc -}}
{{- else -}}
{{- randAlphaNum $len -}}
{{- end -}}
{{- end }}

{{- define "orders.postgresql.password" -}}
{{- if not (empty .Values.app.persistence.secret.password) -}}
    {{- .Values.app.persistence.secret.password | b64enc -}}
{{- else -}}
    {{- include "getOrGeneratePass" (dict "Namespace" .Release.Namespace "Kind" "Secret" "Name" .Values.app.persistence.secret.name "Key" "RETAIL_ORDERS_PERSISTENCE_PASSWORD") -}}
{{- end -}}
{{- end -}}

{{- define "orders.postgresql.endpoint" -}}
{{- if .Values.postgresql.create -}}
{{ include "orders.postgresql.fullname" . }}:{{ .Values.postgresql.service.port }}
{{- else }}
{{- .Values.app.persistence.endpoint -}}
{{- end -}}
{{- end -}}

{{- define "orders.rabbitmq.fullname" -}}
{{- include "orders.fullname" . }}-rabbitmq
{{- end -}}

{{/*
Common labels for rabbitmq
*/}}
{{- define "orders.rabbitmq.labels" -}}
helm.sh/chart: {{ include "orders.chart" . }}
{{ include "orders.rabbitmq.selectorLabels" . }}
{{- if .Chart.AppVersion }}
app.kubernetes.io/version: {{ .Chart.AppVersion | quote }}
{{- end }}
app.kubernetes.io/managed-by: {{ .Release.Service }}
{{- end }}

{{/*
Selector labels for rabbitmq
*/}}
{{- define "orders.rabbitmq.selectorLabels" -}}
app.kubernetes.io/name: {{ include "orders.fullname" . }}
app.kubernetes.io/instance: {{ .Release.Name }}
app.kubernetes.io/component: rabbitmq
app.kubernetes.io/owner: retail-store-sample
{{- end }}

{{- define "orders.rabbitmq.addresses" -}}
{{- if .Values.rabbitmq.create -}}
{{ include "orders.rabbitmq.fullname" . }}:{{ .Values.rabbitmq.service.amqp.port }}
{{- else }}
{{- join "," .Values.app.messaging.rabbitmq.addresses }}
{{- end -}}
{{- end -}}